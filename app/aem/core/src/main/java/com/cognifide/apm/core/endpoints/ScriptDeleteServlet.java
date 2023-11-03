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

import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.endpoints.response.ResponseEntity;
import com.cognifide.apm.core.endpoints.utils.RequestProcessor;
import java.io.IOException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/apm/scripts/delete",
        Property.METHOD + "POST",
        Property.DESCRIPTION + "APM Script Delete Servlet",
        Property.VENDOR
    }
)
public class ScriptDeleteServlet extends SlingAllMethodsServlet {

  @Reference
  private ModelFactory modelFactory;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    RequestProcessor.process(modelFactory, ScriptDeleteForm.class, request, response, (form, resourceResolver) -> {
      try {
        Session session = resourceResolver.adaptTo(Session.class);
        for (String path : form.getPaths()) {
          if (session.nodeExists(path)) {
            session.removeItem(path);
          }
        }
        session.save();
        return ResponseEntity.ok("Item(s) successfully deleted");
      } catch (Exception e) {
        return ResponseEntity.badRequest(StringUtils.defaultString(e.getMessage(), "Errors while deleting item(s)"));
      }
    });
  }
}
