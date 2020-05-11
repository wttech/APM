/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.utils.Pagination;
import com.google.common.primitives.Ints;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = Servlet.class,
    property = {
        Property.RESOURCE_TYPE + "apm/datasource/history",
        Property.DESCRIPTION + "Provides data source for history page",
        Property.VENDOR
    }
)
public class HistoryDatasourceServlet extends SlingSafeMethodsServlet {

  private static final int DEFAULT_LIMIT = 10;

  @Reference
  private History history;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    Pagination pagination = createPagination(request);
    List<Resource> allHistoryResources = pagination.getPage(history.findAllResources(request.getResourceResolver()))
        .stream()
        .map(ResourceTypeWrapper::new)
        .collect(Collectors.toList());
    DataSource dataSource = new SimpleDataSource(allHistoryResources.iterator());
    request.setAttribute(DataSource.class.getName(), dataSource);
  }

  private Pagination createPagination(SlingHttpServletRequest request) {
    String[] selectors = request.getRequestPathInfo().getSelectors();
    if (selectors == null || selectors.length < 2) {
      return new Pagination(0, DEFAULT_LIMIT + 1);
    } else {
      Integer offset = Ints.tryParse(selectors[0]);
      Integer limit = Ints.tryParse(selectors[1]);
      return new Pagination(offset, limit + 1);
    }
  }

  private class ResourceTypeWrapper extends ResourceWrapper {

    ResourceTypeWrapper(Resource resource) {
      super(resource);
    }

    @Override
    public String getResourceType() {
      return "apm/components/historyRow";
    }
  }
}
