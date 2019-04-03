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

import static com.cognifide.cq.cqsm.core.servlets.ScriptResultServlet.EXECUTION_RESULT_SERVLET_PATH;

import com.cognifide.cq.cqsm.core.Cqsm;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(paths = {EXECUTION_RESULT_SERVLET_PATH}, methods = {"POST"})
@Service
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Execution result Servlet"),
    @Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)
})
public class ScriptResultServlet extends SlingAllMethodsServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScriptResultServlet.class);

  public static final String EXECUTION_RESULT_SERVLET_PATH = "/bin/cqsm/executionResultDownload";

  @Override
  protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws IOException {

    String fileName = request.getParameter("filename");
    String content = request.getParameter("content");

    if (fileName == null || fileName.length() == 0) {
      LOGGER.error("Parameter fileName is required");
      return;
    }

    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition",
        "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

    InputStream input = IOUtils.toInputStream(content);
    IOUtils.copy(input, response.getOutputStream());
  }

}
