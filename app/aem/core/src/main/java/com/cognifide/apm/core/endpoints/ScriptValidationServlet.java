/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.endpoints;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.scripts.TransientScript;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.endpoints.response.ResponseEntity;
import com.cognifide.apm.core.logger.Position;
import com.cognifide.apm.core.logger.ProgressEntry;
import com.cognifide.apm.core.scripts.ScriptStorageException;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/apm/scripts/validate",
        Property.METHOD + "POST",
        Property.DESCRIPTION + "APM Script Validation Servlet",
        Property.VENDOR
    }
)
public class ScriptValidationServlet extends AbstractFormServlet<ScriptValidationForm> {

  @Reference
  private ScriptManager scriptManager;

  @Override
  protected ResponseEntity process(ScriptValidationForm form, ResourceResolver resolver) throws Exception {
    ResponseEntity responseEntity;
    try {
      Script script = TransientScript.create(form.getPath(), form.getContent());
      ExecutionResult result = scriptManager.process(script, ExecutionMode.VALIDATION, resolver);
      if (result.isSuccess()) {
        responseEntity = ResponseEntity.ok("Script passes validation", ImmutableMap.of(
            "valid", true
        ));
      } else {
        List<String> validationErrors = transformToValidationErrors(result);
        responseEntity = ResponseEntity.ok("Script does not pass validation", ImmutableMap.of(
            "valid", false,
            "errors", validationErrors
        ));
      }
    } catch (ScriptStorageException e) {
      responseEntity = ResponseEntity.ok(StringUtils.defaultString(e.getMessage(), "Errors while saving script"), ImmutableMap.of(
          "errors", e.getErrors()
      ));
    }
    return responseEntity;
  }

  private List<String> transformToValidationErrors(ExecutionResult result) {
    return result.getEntries()
        .stream()
        .filter(entry -> entry.getStatus() == Status.ERROR)
        .filter(entry -> !entry.getMessages().isEmpty())
        .flatMap(this::transformToErrors)
        .collect(Collectors.toList());
  }

  private Stream<String> transformToErrors(ExecutionResult.Entry entry) {
    String positionPrefix = positionPrefix(entry);
    return entry.getMessages()
        .stream()
        .map(message -> positionPrefix + message);
  }

  private String positionPrefix(ExecutionResult.Entry entry) {
    Position position = entry instanceof ProgressEntry ? ((ProgressEntry) entry).getPosition() : null;
    return position != null ? String.format("Invalid line %s: ", position.getLine()) : "";
  }
}
