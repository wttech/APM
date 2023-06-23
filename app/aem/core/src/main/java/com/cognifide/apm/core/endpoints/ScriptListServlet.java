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

import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.endpoints.dto.ScriptDto;
import com.cognifide.apm.core.utils.ServletUtils;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/apm/scripts",
        Property.METHOD + "GET",
        Property.DESCRIPTION + "APM Script List Servlet",
        Property.VENDOR
    }
)
public class ScriptListServlet extends SlingAllMethodsServlet {

  @Reference
  private ScriptFinder scriptFinder;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
    List<ScriptDto> scripts = scriptFinder.findAll(request.getResourceResolver())
        .stream()
        .map(ScriptDto::new)
        .sorted(Comparator.comparing(ScriptDto::getPath))
        .collect(Collectors.toList());
    ServletUtils.writeJson(response, scripts);
  }
}
