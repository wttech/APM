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
import org.osgi.service.component.annotations.Reference
import javax.servlet.Servlet
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.utils.ServletUtils
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import javax.servlet.http.HttpServletResponse
import com.cognifide.apm.api.services.ExecutionMode
import com.cognifide.apm.core.progress.ProgressHelper
import javax.jcr.RepositoryException

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/run",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Run Script Servlet",
            Property.VENDOR
        ])
class ScriptRunServlet : SlingAllMethodsServlet() {

    @Reference
    @Transient
    private lateinit var scriptManager: ScriptManager

    @Reference
    @Transient
    private lateinit var scriptFinder: ScriptFinder

    override fun doPost(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val searchPath = request.getParameter("file")
        val modeName = request.getParameter("mode")
        val resourceResolver = request.resourceResolver

        if (StringUtils.isEmpty(modeName)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            ServletUtils.writeMessage(response, "error", "Running mode not specified.")
        }

        val script = scriptFinder.find(searchPath, resourceResolver)
        if (script == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ServletUtils.writeMessage(response, "error", "Script not found: $searchPath")
        }

        try {
            val mode = ExecutionMode.fromString(modeName, ExecutionMode.DRY_RUN)
            val result = scriptManager.process(script, mode, resourceResolver)

            if (result.isSuccess()) {
                ServletUtils.writeJson(response, ProgressHelper.toJson(result.entries))
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                ServletUtils.writeJson(response, ProgressHelper.toJson(result.lastError))
            }
        } catch (e: RepositoryException) {
            ServletUtils.writeMessage(response, "error", "Script cannot be executed because of repository error: ${e.message}")
        }
    }
}