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
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.MissingElementException;
import org.apache.sling.models.factory.MissingElementsException;
import org.apache.sling.models.factory.ModelFactory;

public final class RequestProcessor {

  private RequestProcessor() {
    // intentionally empty
  }

  public static <T> void process(ModelFactory modelFactory, Class<T> formClass, SlingHttpServletRequest httpRequest, SlingHttpServletResponse httpResponse, BiFunction<T, ResourceResolver, ResponseEntity> processFunc) throws IOException {
    try {
      T form = modelFactory.createModel(httpRequest, formClass);
      ResponseEntity response = processFunc.apply(form, httpRequest.getResourceResolver());

      httpResponse.setStatus(response.getStatusCode());
      ServletUtils.writeJson(httpResponse, response.getBody());
    } catch (MissingElementsException e) {
      httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Map<String, Object> params = new HashMap<>();
      params.put("message", "Bad request");
      params.put("errors", toErrors(e));
      ServletUtils.writeJson(httpResponse, params);
    } catch (Exception e) {
      httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      ServletUtils.writeJson(httpResponse, Collections.singletonMap(
          "message", StringUtils.defaultString(e.getMessage())
      ));
    }
  }

  private static List<String> toErrors(MissingElementsException e) {
    return e.getMissingElements()
        .stream()
        .map(MissingElementException::getElement)
        .filter(Objects::nonNull)
        .map(element -> element.getAnnotation(RequestParameter.class))
        .filter(Objects::nonNull)
        .map(annotation -> String.format("Missing required parameter: %s", annotation.value()))
        .collect(Collectors.toList());
  }
}
