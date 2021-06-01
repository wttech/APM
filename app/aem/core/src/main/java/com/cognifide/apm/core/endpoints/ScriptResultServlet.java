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

import com.cognifide.apm.core.Property;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

@Component(
    immediate = true,
    service = Servlet.class,
    property = {
        Property.PATH + ScriptResultServlet.EXECUTION_RESULT_SERVLET_PATH,
        Property.METHOD + HttpConstants.METHOD_POST,
        Property.DESCRIPTION + "Execution result Servlet",
        Property.VENDOR
    }
)
@Slf4j
public class ScriptResultServlet extends SlingAllMethodsServlet {

  public static final String EXECUTION_RESULT_SERVLET_PATH = "/bin/cqsm/executionResultDownload";

  @Override
  protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws IOException {

    String fileName = request.getParameter("filename");
    String content = request.getParameter("content");

    if (fileName == null || fileName.length() == 0) {
      log.error("Parameter fileName is required");
      return;
    }

    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition",
        "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));

    InputStream input = IOUtils.toInputStream(content);
    IOUtils.copy(input, response.getOutputStream());
  }

}
