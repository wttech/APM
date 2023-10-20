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

import com.cognifide.apm.core.Apm;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.endpoints.response.ResponseEntity;
import com.cognifide.apm.core.endpoints.utils.RequestProcessor;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import java.io.IOException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/apm/scripts/move",
        Property.METHOD + "POST",
        Property.DESCRIPTION + "APM Script Move Servlet",
        Property.VENDOR
    }
)
public class ScriptMoveServlet extends SlingAllMethodsServlet {

  @Reference
  private ModelFactory modelFactory;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    RequestProcessor.process(modelFactory, ScriptMoveForm.class, request, response, (form, resourceResolver) -> {
      try {
        Session session = resourceResolver.adaptTo(Session.class);
        String dest = StringUtils.defaultIfEmpty(form.getDest(), StringUtils.substringBeforeLast(form.getPath(), "/"));
        String rename = containsExtension(form.getPath())
            ? (form.getRename() + (containsExtension(form.getRename()) ? "" : Apm.FILE_EXT))
            : JcrUtil.createValidName(form.getRename());
        String destPath = String.format("%s/%s", dest, rename);
        if (!StringUtils.equals(form.getPath(), destPath)) {
          destPath = createUniquePath(destPath, resourceResolver);
          session.move(form.getPath(), destPath);
          session.save();
        }
        if (!containsExtension(form.getPath())) {
          ValueMap valueMap = resourceResolver.getResource(destPath).adaptTo(ModifiableValueMap.class);
          valueMap.put(JcrConstants.JCR_TITLE, form.getRename());
        }
        resourceResolver.commit();
        return ResponseEntity.ok("Item successfully moved");
      } catch (Exception e) {
        return ResponseEntity.badRequest(StringUtils.defaultString(e.getMessage(), "Errors while moving item"));
      }
    });
  }

  private boolean containsExtension(String path) {
    return path.endsWith(Apm.FILE_EXT);
  }

  private String createUniquePath(String pathWithExtension, ResourceResolver resolver) {
    String path = StringUtils.substringBeforeLast(pathWithExtension, Apm.FILE_EXT);
    String extension = containsExtension(pathWithExtension) ? Apm.FILE_EXT : "";
    int counter = 0;
    while (resolver.getResource(path + (counter > 0 ? counter : "") + extension) != null) {
      counter++;
    }
    return path + (counter > 0 ? counter : "") + extension;
  }
}
