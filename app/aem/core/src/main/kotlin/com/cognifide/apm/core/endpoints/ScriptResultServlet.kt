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
package com.cognifide.apm.core.endpoints

import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.osgi.service.component.annotations.Component
import javax.servlet.Servlet
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.utils.ServletUtils
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import com.cognifide.apm.api.services.ExecutionMode
import com.cognifide.apm.core.progress.ProgressHelper
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.net.URLEncoder

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + ScriptResultServlet.EXECUTION_RESULT_SERVLET_PATH,
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Execution Result Servlet",
            Property.VENDOR
        ])
class ScriptResultServlet : SlingAllMethodsServlet() {

    private val logger = LoggerFactory.getLogger(ScriptDownloadServlet::class.java)

    override fun doPost(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val fileName = request.getParameter("filename")
        val content = request.getParameter("content")

        if (StringUtils.isEmpty(fileName)) {
            logger.error("Parameter filename is required")
            return
        }

        response.setContentType("application/octet-stream")
        response.setHeader("Content-Disposition", "attachment; filename=${URLEncoder.encode(fileName, "UTF-8")}")

        val input: InputStream = IOUtils.toInputStream(content)
        IOUtils.copy(input, response.outputStream)
    }

    companion object {
        const val EXECUTION_RESULT_SERVLET_PATH = "/bin/apm/executionResultDownload"
    }
}