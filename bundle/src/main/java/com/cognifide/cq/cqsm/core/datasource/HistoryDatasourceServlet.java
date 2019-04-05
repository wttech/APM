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
package com.cognifide.cq.cqsm.core.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.cognifide.cq.cqsm.api.history.HistoryEntry;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.history.History;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;

@SlingServlet(resourceTypes = "apm/datasource/history")
@Service
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Provides data source for history page"),
    @Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)
})
public class HistoryDatasourceServlet extends SlingSafeMethodsServlet {

  @Reference
  private History history;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    final List<Resource> allHistoryResources = history.findAllResources(request.getResourceResolver())
        .stream()
        .sorted(Comparator.comparing(this::getExecutionTime, Comparator.reverseOrder()))
        .map(ResourceTypeWrapper::new)
        .collect(Collectors.toList());
    DataSource dataSource = new SimpleDataSource(allHistoryResources.iterator());
    request.setAttribute(DataSource.class.getName(), dataSource);
  }

  private Date getExecutionTime(Resource resource) {
    return resource.getValueMap().get(HistoryEntry.EXECUTION_TIME, Date.class);
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
