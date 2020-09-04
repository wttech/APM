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

import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.osgi.service.component.annotations.Component
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import javax.jcr.RepositoryException
import javax.jcr.Session
import javax.servlet.Servlet
import com.cognifide.apm.core.Property
import org.apache.sling.api.servlets.SlingSafeMethodsServlet

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/scripts/fileDownload",
            Property.METHOD + "GET",
            Property.DESCRIPTION + "APM File Download Servlet",
            Property.VENDOR
        ])
class ScriptDownloadServlet : SlingSafeMethodsServlet() {

    private val logger = LoggerFactory.getLogger(ScriptDownloadServlet::class.java)

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val fileName = request.getParameter("filename")
        val filePath = request.getParameter("filepath")
        val mode = request.getParameter("mode")

        try {
            val resourceResolver = request.resourceResolver
            val session = resourceResolver.adaptTo(Session::class.java)!!

            if (!("view").equals(mode)) {
                response.setContentType("application/octet-stream")
                response.setHeader("Content-Disposition",
                        "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"))
            }

            val path = StringUtils.replace(filePath, "_jcr_content", "jcr:content")
            val jcrContent = session.getNode("$path/jcr:content")
            val input = jcrContent.getProperty("jcr:data").getBinary().getStream()

            session.save()
            var read : Int
            val bytes = ByteArray(BYTES_DOWNLOAD)
            val os = response.outputStream

            while (input.read(bytes).also { read = it } != -1) {
                os.write(bytes, 0, read)
            }
            input.close()
            os.flush()
            os.close()
        } catch (e: RepositoryException) {
            logger.error(e.message, e)
            //todo change redirect /apm/?
            response.sendRedirect("/etc/cqsm.html")
        }
    }

    companion object {
        const val BYTES_DOWNLOAD = 1024
    }
}
