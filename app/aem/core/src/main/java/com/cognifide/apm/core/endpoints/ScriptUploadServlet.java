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
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.endpoints.dto.ScriptDto;
import com.cognifide.apm.core.endpoints.response.ResponseEntity;
import com.cognifide.apm.core.endpoints.utils.RequestProcessor;
import com.cognifide.apm.core.scripts.ScriptStorage;
import com.cognifide.apm.core.scripts.ScriptStorageException;
import java.io.IOException;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/apm/scripts/upload",
        Property.METHOD + "POST",
        Property.DESCRIPTION + "APM Script Upload Servlet",
        Property.VENDOR
    }
)
public class ScriptUploadServlet extends SlingAllMethodsServlet {

  @Reference
  private ScriptStorage scriptStorage;

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ModelFactory modelFactory;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    RequestProcessor.process(modelFactory, ScriptUploadForm.class, request, response, (form, resourceResolver) -> {
      try {
        Script script = scriptStorage.save(form, resourceResolver);
        scriptManager.process(script, ExecutionMode.VALIDATION, resourceResolver);
        return ResponseEntity.ok("File successfully saved")
            .addEntry("uploadedScript", new ScriptDto(script));
      } catch (ScriptStorageException e) {
        return ResponseEntity.badRequest(StringUtils.defaultString(e.getMessage(), "Errors while saving script"))
            .addEntry("errors", e.getErrors());
      } catch (PersistenceException | RepositoryException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
