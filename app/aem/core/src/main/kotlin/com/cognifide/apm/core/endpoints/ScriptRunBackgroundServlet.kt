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

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.endpoints.params.BackgroundJobParameters
import com.cognifide.apm.core.jobs.ScriptRunnerJobManager
import com.cognifide.apm.core.utils.ServletUtils
import com.google.common.collect.ImmutableMap
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.apache.sling.event.jobs.Job
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.servlet.Servlet

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/run-background",
            Property.METHOD + "GET",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Servlet for running scripts in background and checking theirs status",
            Property.VENDOR
        ])
class ScriptRunBackgroundServlet : SlingAllMethodsServlet() {

    @Reference
    @Transient
    private lateinit var scriptRunnerJobManager: ScriptRunnerJobManager

    @Reference
    @Transient
    private lateinit var scriptFinder: ScriptFinder

    override fun doPost(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val searchPath = request.getParameter(FILE_REQUEST_PARAMETER)
        val resourceResolver = request.resourceResolver
        val script: Script = scriptFinder.find(searchPath, resourceResolver)

        val isValid: Boolean = script.isValid()
        val isExecutable: Boolean = script.isLaunchEnabled()

        if (!(isValid && isExecutable)) {
            ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE,
                    """Script $searchPath cannot be processed. Script needs to be executable and valid.
                    Actual script status: valid - $isValid, executable - $isExecutable""")
        }

        val parameters = getParameters(request, response) ?: return

        val job: Job =  scriptRunnerJobManager.scheduleJob(parameters)
        ServletUtils.writeMessage(response, BACKGROUND_RESPONSE_TYPE, BACKGROUND_RESPONSE_TYPE, createMapWithJobIdKey(job))
    }

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val id = request.getParameter(ID_REQUEST_PARAMETER) ?: return
        val jobProgressOutput = scriptRunnerJobManager.checkJobStatus(id)
        ServletUtils.writeJson(response, jobProgressOutput)
    }

    private fun getParameters(request: SlingHttpServletRequest, response: SlingHttpServletResponse): BackgroundJobParameters {
        val searchPath = request.getParameter(FILE_REQUEST_PARAMETER)
        val modeName = request.getParameter(MODE_REQUEST_PARAMETER)
        val userName = request.resourceResolver.userID

        if (StringUtils.isEmpty(searchPath)) {
            ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE, "Please set the script file name: -d \"file=[name]\"")
        }
        if (StringUtils.isEmpty(modeName)) {
            ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE, "Running mode not specified")
        }
        return BackgroundJobParameters(searchPath, modeName, userName!!)
    }

    private fun createMapWithJobIdKey(job: Job): Map<String, Any> {
        return ImmutableMap.builder<String, Any>().put(ID_REQUEST_PARAMETER, job.id).build()
    }

    companion object {
        const val BACKGROUND_RESPONSE_TYPE = "background"
        const val ERROR_RESPONSE_TYPE = "error"
        const val FILE_REQUEST_PARAMETER = "file"
        const val MODE_REQUEST_PARAMETER = "mode"
        const val ID_REQUEST_PARAMETER = "id"
    }
}