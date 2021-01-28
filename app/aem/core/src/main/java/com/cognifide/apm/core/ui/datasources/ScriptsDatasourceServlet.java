
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
package com.cognifide.apm.core.ui.datasources;

import static com.cognifide.apm.core.ui.models.ScriptsRowModel.SCRIPTS_ROW_RESOURCE_TYPE;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.scripts.ScriptModel;
import com.cognifide.apm.core.ui.models.ScriptsRowModel;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

@Component(
    immediate = true,
    service = Servlet.class,
    property = {
        Property.RESOURCE_TYPE + "apm/datasource/scripts",
        Property.METHOD + "GET",
        Property.DESCRIPTION + "APM Scripts Data Source Servlet",
        Property.VENDOR
    }
)
public class ScriptsDatasourceServlet extends SlingSafeMethodsServlet {

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    String path = request.getRequestPathInfo().getSuffix();
    List<Resource> scripts = new ArrayList<>();
    Resource resource = request.getResourceResolver().getResource(path);
    for (Resource child : resource.getChildren()) {
      if (ScriptsRowModel.isFolder(child) || ScriptModel.isScript(child)) {
        scripts.add(new ResourceTypeWrapper(child));
      }
    }
    DataSource dataSource = new SimpleDataSource(scripts.iterator());
    request.setAttribute(DataSource.class.getName(), dataSource);
  }

  private class ResourceTypeWrapper extends ResourceWrapper {

    ResourceTypeWrapper(Resource resource) {
      super(resource);
    }

    @Override
    public String getResourceType() {
      return SCRIPTS_ROW_RESOURCE_TYPE;
    }
  }

}
