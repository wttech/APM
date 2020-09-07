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
import com.cognifide.apm.core.history.History
import com.cognifide.apm.core.history.ScriptHistory
import com.cognifide.apm.core.history.ScriptHistoryImpl
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.utils.ServletUtils
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import com.cognifide.apm.api.services.ExecutionMode
import com.cognifide.apm.core.progress.ProgressHelper
import org.osgi.service.component.annotations.Reference

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/lastSummary",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Summary Redirect Servlet",
            Property.VENDOR
        ])
class SummaryRedirectServlet : SlingAllMethodsServlet() {

    @Reference
    @Transient
    private lateinit var history: History

    @Reference
    @Transient
    private lateinit var scriptFinder: ScriptFinder

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val mode = StringUtils.defaultString(request.requestPathInfo.selectorString)
        val scriptPath = request.requestPathInfo.suffix

        val scriptHistory: ScriptHistory? = scriptFinder.find(scriptPath, request.resourceResolver)?.let {
            script -> history.findScriptHistory(request.resourceResolver, script)
        } ?: ScriptHistoryImpl.empty(scriptPath)

        val lastSummaryPath = getLastSummaryPath(scriptHistory!!, mode)
        if (lastSummaryPath.isNotEmpty()) {
            response.sendRedirect("/apm/summary.html$lastSummaryPath")
        } else {
            response.sendRedirect("/apm/history.html")
        }
    }

    private fun getLastSummaryPath(scriptHistory: ScriptHistory, mode: String) : String {
        return when (mode.toLowerCase()) {
            "localdryrun" -> scriptHistory.getLastLocalDryRunPath()
            "localrun" -> scriptHistory.getLastLocalRunPath()
            "remoteautomaticrun" -> scriptHistory.getLastRemoteRunPath()
            else -> StringUtils.EMPTY
        }
    }
}