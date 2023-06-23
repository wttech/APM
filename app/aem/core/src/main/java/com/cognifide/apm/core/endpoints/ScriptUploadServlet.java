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
import com.cognifide.apm.core.scripts.ScriptStorage;
import com.cognifide.apm.core.scripts.ScriptStorageException;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet

@Component(
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/apm/scripts/upload",
        Property.METHOD + "POST",
        Property.DESCRIPTION + "APM Script Upload Servlet",
        Property.VENDOR
    }
)
public class ScriptUploadServlet extends AbstractFormServlet<ScriptUploadForm> {

  @Reference
  private transient ScriptStorage scriptStorage;

  @Reference
  private transient ScriptManager scriptManager;

  @Override
  protected Class<ScriptUploadForm> getFormClass() {
    return ScriptUploadForm.class;
  }

  @Override
  protected ResponseEntity doPost(ScriptUploadForm form, ResourceResolver resolver) throws Exception {
    ResponseEntity responseEntity;
    try {
      Script script = scriptStorage.save(form, resolver);
      scriptManager.process(script, ExecutionMode.VALIDATION, resolver);
      responseEntity = ResponseEntity.ok("File successfully saved", ImmutableMap.of(
          "uploadedScript", new ScriptDto(script)
      ));
    } catch (ScriptStorageException e) {
      responseEntity = ResponseEntity.badRequest(StringUtils.defaultString(e.getMessage(), "Errors while saving script"), ImmutableMap.of(
          "errors", e.getErrors()
      ));
    }
    return responseEntity;
  }
}
