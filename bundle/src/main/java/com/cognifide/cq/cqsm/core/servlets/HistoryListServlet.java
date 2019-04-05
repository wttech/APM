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
package com.cognifide.cq.cqsm.core.servlets;

import com.cognifide.cq.cqsm.api.history.HistoryEntry;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.history.History;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;

@SlingServlet(paths = {"/bin/cqsm/history"}, methods = {"GET"})
@Service
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM History List Servlet"),
    @Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)
})
public class HistoryListServlet extends SlingAllMethodsServlet {

  @Reference
  private History history;

  @Override
  protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws IOException {

    String filter = request.getParameter("filter");
    List<HistoryEntry> executions = new ArrayList<>(history.findAll());
    Collections.sort(executions);
    if (StringUtils.isNotBlank(filter)) {
      CollectionUtils.filter(executions, new ExecutionHistoryFilter(filter));
    }
    ServletUtils.writeJson(response, executions);
  }

  private static class ExecutionHistoryFilter implements Predicate {

    private static final String FILTER_AUTOMATIC_RUN = "automatic run";

    private static final String FILTER_AUTHOR = "author";

    private static final String FILTER_PUBLISH = "publish";

    private final String filterType;

    private ExecutionHistoryFilter(String filterType) {
      this.filterType = filterType;
    }

    @Override
    public boolean evaluate(Object object) {
      HistoryEntry executionModel = (HistoryEntry) object;
      String value;
      switch (filterType) {
        case FILTER_AUTHOR:
        case FILTER_PUBLISH: {
          value = executionModel.getInstanceType();
        }
        break;
        case FILTER_AUTOMATIC_RUN: {
          value = executionModel.getExecutor();
        }
        break;
        default: {
          value = null;
        }
      }
      return filterType.equals(value);
    }
  }
}
