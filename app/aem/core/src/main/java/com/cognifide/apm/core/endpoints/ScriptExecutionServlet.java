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
package com.cognifide.apm.core.endpoints;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.endpoints.response.ResponseEntity;
import com.cognifide.apm.core.endpoints.utils.RequestProcessor;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.services.async.AsyncScriptExecutor;
import com.cognifide.apm.core.services.async.ExecutionStatus;
import com.cognifide.apm.core.services.async.FinishedFailedExecution;
import com.cognifide.apm.core.services.async.FinishedSuccessfulExecution;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import com.google.common.collect.ImmutableMap;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

@Component(
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/apm/scripts/exec",
        Property.METHOD + "POST",
        Property.DESCRIPTION + "APM Script Execution Servlet",
        Property.VENDOR
    }
)
public class ScriptExecutionServlet extends SlingAllMethodsServlet {

  @Reference
  private transient ScriptFinder scriptFinder;

  @Reference
  private transient ScriptManager scriptManager;

  @Reference
  private transient AsyncScriptExecutor asyncScriptExecutor;

  @Reference
  private transient ModelFactory modelFactory;

  @Reference
  private transient ResourceResolverProvider resolverProvider;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
    new RequestProcessor<>(modelFactory, ScriptExecutionStatusForm.class).process(request, response, (form, resolver) -> {
      ResponseEntity responseEntity;
      ExecutionStatus status = asyncScriptExecutor.checkStatus(form.getId());
      if (status instanceof FinishedSuccessfulExecution) {
        responseEntity = ResponseEntity.ok("Script successfully executed", ImmutableMap.of(
            "status", status.getStatus(),
            "output", ((FinishedSuccessfulExecution) status).getEntries(),
            "path", ((FinishedSuccessfulExecution) status).getPath()
        ));
      } else if (status instanceof FinishedFailedExecution) {
        responseEntity = ResponseEntity.internalServerError("Errors while executing script", ImmutableMap.of(
            "status", status.getStatus(),
            "output", ((FinishedFailedExecution) status).getEntries(),
            "path", ((FinishedFailedExecution) status).getPath(),
            "errors", ((FinishedFailedExecution) status).getError().getMessages()
        ));
      } else {
        responseEntity = ResponseEntity.ok("Script is still being processed", ImmutableMap.of(
            "status", status.getStatus()
        ));

      }
      return responseEntity;
    });
  }

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
    String executor = request.getResourceResolver().getUserID();
    new RequestProcessor<>(modelFactory, ScriptExecutionForm.class).process(request, response, (form, resolver) ->
        SlingHelper.resolve(resolverProvider, resolver1 -> executeScript(form, resolver1, executor))
    );
  }

  private ResponseEntity executeScript(ScriptExecutionForm form, ResourceResolver resourceResolver, String executor) {
    ResponseEntity responseEntity;
    try {
      Script script = scriptFinder.find(form.getScript(), resourceResolver);
      if (script == null) {
        responseEntity = ResponseEntity.notFound(String.format("Script not found: %s", form.getScript()), Collections.emptyMap());
      } else if (!script.isLaunchEnabled()) {

        responseEntity = ResponseEntity.internalServerError("Script cannot be executed because it is disabled", Collections.emptyMap());
      } else if (!script.isValid()) {

        responseEntity = ResponseEntity.internalServerError("Script cannot be executed because it is invalid", Collections.emptyMap());
      } else {
        responseEntity = form.isAsync() ? asyncExecute(script, form, executor) : syncExecute(script, form, resourceResolver, executor);
      }
    } catch (PersistenceException | RepositoryException e) {
      responseEntity = ResponseEntity.internalServerError(String.format("Script cannot be executed because of repository error: %s", e.getMessage()), Collections.emptyMap());
    }
    return responseEntity;

  }

  private ResponseEntity asyncExecute(Script script, ScriptExecutionForm form, String executor) {
    String id = asyncScriptExecutor.process(script, form.getExecutionMode(), form.getCustomDefinitions(), executor);
    return ResponseEntity.ok("Script successfully queued for async execution", ImmutableMap.of(
        "id", id
    ));
  }

  private ResponseEntity syncExecute(Script script, ScriptExecutionForm form, ResourceResolver resourceResolver, String executor) throws PersistenceException, RepositoryException {
    ResponseEntity responseEntity;
    ExecutionResult result = scriptManager.process(script, form.getExecutionMode(), form.getCustomDefinitions(), resourceResolver, executor);
    if (result.isSuccess()) {
      responseEntity = ResponseEntity.ok("Script successfully executed", ImmutableMap.of(
          "output", result.getEntries()
      ));
    } else {
      responseEntity = ResponseEntity.internalServerError("Errors while executing script", ImmutableMap.of(
          "output", result.getEntries(),
          "errors", result.getLastError().getMessages()
      ));
    }
    return responseEntity;
  }
}
