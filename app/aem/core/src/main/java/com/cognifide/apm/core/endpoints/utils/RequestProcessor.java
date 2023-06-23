/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.endpoints.utils;

import com.cognifide.apm.core.endpoints.params.RequestParameter;
import com.cognifide.apm.core.endpoints.response.ResponseEntity;
import com.cognifide.apm.core.utils.ServletUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.factory.MissingElementException;
import org.apache.sling.models.factory.MissingElementsException;
import org.apache.sling.models.factory.ModelFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestProcessor<T> {

  public void process(SlingHttpServletRequest request, SlingHttpServletResponse response, ModelFactory modelFactory, ProcessCallback<T> processCallback) throws IOException {
    ResponseEntity responseEntity;
    try {
      ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
      T form = modelFactory.createModel(request, (Class<T>) type.getActualTypeArguments()[0]);
      responseEntity = processCallback.process(form, request.getResourceResolver());
    } catch (MissingElementsException e) {
      responseEntity = ResponseEntity.badRequest("Bad request", ImmutableMap.of(
          "errors", toErrors(e)
      ));
    } catch (Exception e) {
      responseEntity = ResponseEntity.internalServerError(StringUtils.defaultString(e.getMessage()), Collections.emptyMap());
    }
    response.setStatus(responseEntity.getStatusCode());
    ServletUtils.writeJson(response, responseEntity.getBody());
  }

  private List<String> toErrors(MissingElementsException e) {
    return e.getMissingElements()
        .stream()
        .map(MissingElementException::getElement)
        .filter(Objects::nonNull)
        .map(x -> x.getAnnotation(RequestParameter.class))
        .filter(Objects::nonNull)
        .map(annotation -> String.format("Missing required parameter: %s", annotation.value()))
        .collect(Collectors.toList());
  }
}
