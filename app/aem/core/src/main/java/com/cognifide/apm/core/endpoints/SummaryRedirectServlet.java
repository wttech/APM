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

import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.history.ScriptHistory;
import com.cognifide.apm.core.history.ScriptHistoryImpl;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.Servlet;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/cqsm/lastSummary",
        Property.EXTENSION + "html",
        Property.DESCRIPTION + "CQSM Summary Redirect Servlet",
        Property.VENDOR
    }
)
public class SummaryRedirectServlet extends SlingAllMethodsServlet {

  @Reference
  private History history;
  @Reference
  private ScriptFinder scriptFinder;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    String mode = StringUtils.defaultString(request.getRequestPathInfo().getSelectorString());
    String scriptPath = request.getRequestPathInfo().getSuffix();
    ScriptHistory scriptHistory = Optional.ofNullable(scriptFinder.find(scriptPath, request.getResourceResolver()))
        .map(script -> history.findScriptHistory(request.getResourceResolver(), script))
        .orElse(ScriptHistoryImpl.empty(scriptPath));
    String lastSummaryPath = getLastSummaryPath(scriptHistory, mode);
    if (!lastSummaryPath.isEmpty()) {
      response.sendRedirect("/apm/summary.html" + lastSummaryPath);
    } else {
      response.sendRedirect("/apm/history.html");
    }
  }

  private String getLastSummaryPath(ScriptHistory scriptHistory, String mode) {
    switch (mode.toLowerCase()) {
      case "localdryrun":
        return scriptHistory.getLastLocalDryRunPath();
      case "localrun":
        return scriptHistory.getLastLocalRunPath();
      case "remoteautomaticrun":
        return scriptHistory.getLastRemoteRunPath();
      default:
        return StringUtils.EMPTY;
    }
  }
}
