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
import com.cognifide.apm.core.utils.sling.ResolveException;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.io.IOException;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
  private ScriptFinder scriptFinder;

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private AsyncScriptExecutor asyncScriptExecutor;

  @Reference
  private ModelFactory modelFactory;

  @Reference
  private ResourceResolverProvider resolverProvider;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    RequestProcessor.process(modelFactory, ScriptExecutionStatusForm.class, request, response, (form, resourceResolver) -> {
      ExecutionStatus status = asyncScriptExecutor.checkStatus(form.getId());
      if (status instanceof ExecutionStatus.FinishedSuccessfulExecution) {
        return ResponseEntity.ok("Script successfully executed")
            .addEntry("status", status.getStatus())
            .addEntry("output", ((ExecutionStatus.FinishedSuccessfulExecution) status).getEntries())
            .addEntry("path", ((ExecutionStatus.FinishedSuccessfulExecution) status).getPath())
            .addEntry("timestamp", ((ExecutionStatus.FinishedSuccessfulExecution) status).getTimestamp())
            .addEntry("formattedDate", ((ExecutionStatus.FinishedSuccessfulExecution) status).getFormattedDate());
      } else if (status instanceof ExecutionStatus.FinishedFailedExecution) {
        return ResponseEntity.internalServerError("Errors while executing script")
            .addEntry("status", status.getStatus())
            .addEntry("output", ((ExecutionStatus.FinishedFailedExecution) status).getEntries())
            .addEntry("path", ((ExecutionStatus.FinishedFailedExecution) status).getPath())
            .addEntry("timestamp", ((ExecutionStatus.FinishedFailedExecution) status).getTimestamp())
            .addEntry("formattedDate", ((ExecutionStatus.FinishedFailedExecution) status).getFormattedDate())
            .addEntry("errors", ((ExecutionStatus.FinishedFailedExecution) status).getError().getMessages());
      } else {
        return ResponseEntity.ok("Script is still being processed")
            .addEntry("status", status.getStatus());
      }
    });
  }

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    String executor = request.getResourceResolver().getUserID();
    RequestProcessor.process(modelFactory, ScriptExecutionForm.class, request, response, (form, resourceResolver) -> {
      try {
        return SlingHelper.resolve(resolverProvider, resolver -> executeScript(form, resolver, executor));
      } catch (ResolveException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private ResponseEntity executeScript(ScriptExecutionForm form, ResourceResolver resourceResolver, String executor) {
    try {
      Script script = scriptFinder.find(form.getScript(), resourceResolver);
      if (script == null) {
        return ResponseEntity.notFound(String.format("Script not found: %s", form.getScript()));
      } else if (!script.isLaunchEnabled()) {
        return ResponseEntity.internalServerError("Script cannot be executed because it is disabled");
      } else if (!script.isValid()) {
        return ResponseEntity.internalServerError("Script cannot be executed because it is invalid");
      } else if (form.isAsync()) {
        return asyncExecute(script, form, executor);
      } else {
        return syncExecute(script, form, resourceResolver, executor);
      }
    } catch (RepositoryException | PersistenceException e) {
      return ResponseEntity.internalServerError(String.format("Script cannot be executed because of repository error: %s", e.getMessage()));
    }
  }

  private ResponseEntity asyncExecute(Script script, ScriptExecutionForm form, String executor) {
    String id = asyncScriptExecutor.process(script, form.getExecutionMode(), form.getCustomDefinitions(), executor);
    return ResponseEntity.ok("Script successfully queued for async execution")
        .addEntry("id", id);
  }

  private ResponseEntity syncExecute(Script script, ScriptExecutionForm form, ResourceResolver resourceResolver, String executor) throws PersistenceException, RepositoryException {
    ExecutionResult result = scriptManager.process(script, form.getExecutionMode(), form.getCustomDefinitions(), resourceResolver, executor);
    if (result.isSuccess()) {
      return ResponseEntity.ok("Script successfully executed")
          .addEntry("output", result.getEntries());
    } else {
      return ResponseEntity.internalServerError("Errors while executing script")
          .addEntry("output", result.getEntries())
          .addEntry("errors", result.getLastError().getMessages());
    }
  }
}
