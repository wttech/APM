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
package com.cognifide.apm.core.endpoints

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.endpoints.response.ResponseEntity
import com.cognifide.apm.core.endpoints.response.internalServerError
import com.cognifide.apm.core.endpoints.response.notFound
import com.cognifide.apm.core.endpoints.response.ok
import com.cognifide.apm.core.endpoints.utils.RequestProcessor
import com.cognifide.apm.core.services.async.AsyncScriptExecutor
import com.cognifide.apm.core.services.async.FinishedFailedExecution
import com.cognifide.apm.core.services.async.FinishedSuccessfulExecution
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.apache.sling.models.factory.ModelFactory
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.jcr.RepositoryException
import javax.servlet.Servlet

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/scripts/exec",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Script Execution Servlet",
            Property.VENDOR
        ])
class ScriptExecutionServlet : SlingAllMethodsServlet() {

    @Reference
    @Transient
    private lateinit var scriptFinder: ScriptFinder

    @Reference
    @Transient
    private lateinit var scriptManager: ScriptManager

    @Reference
    @Transient
    private lateinit var asyncScriptExecutor: AsyncScriptExecutor

    @Reference
    @Transient
    private lateinit var modelFactory: ModelFactory

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        RequestProcessor(modelFactory, ScriptExecutionStatusForm::class.java).process(request, response) { form, _ ->
            when (val status = asyncScriptExecutor.checkStatus(form.id)) {
                is FinishedSuccessfulExecution -> ok {
                    message = "Script successfully executed"
                    "status" set status.status
                    "output" set status.entries
                    "path" set status.path
                }
                is FinishedFailedExecution -> internalServerError {
                    message = "Errors while executing script"
                    "status" set status.status
                    "output" set status.entries
                    "path" set status.path
                    errors = status.error.messages ?: listOf()
                }
                else -> ok {
                    message = "Script is still being processed"
                    "status" set status.status
                }
            }
        }
    }

    override fun doPost(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        RequestProcessor(modelFactory, ScriptExecutionForm::class.java).process(request, response) { form, resourceResolver ->
            executeScript(form, resourceResolver)
        }
    }

    private fun executeScript(form: ScriptExecutionForm, resourceResolver: ResourceResolver): ResponseEntity<Any> {
        try {
            val script: Script = scriptFinder.find(form.script, resourceResolver)
                    ?: return notFound { message = "Script not found: ${form.script}" }
            if (!script.isLaunchEnabled) return internalServerError { message = "Script cannot be executed because it is disabled" }
            if (!script.isValid) return internalServerError { message = "Script cannot be executed because it is invalid" }

            return if (form.async) {
                asyncExecute(script, form, resourceResolver)
            } else {
                syncExecute(script, form, resourceResolver)
            }
        } catch (e: RepositoryException) {
            return internalServerError { message = "Script cannot be executed because of repository error: ${e.message}" }
        }
    }

    private fun asyncExecute(script: Script, form: ScriptExecutionForm, resourceResolver: ResourceResolver): ResponseEntity<Any> {
        val id = asyncScriptExecutor.process(script, form.executionMode, form.customDefinitions, resourceResolver)
        return ok {
            message = "Script successfully queued for async execution"
            "id" set id
        }
    }

    private fun syncExecute(script: Script, form: ScriptExecutionForm, resourceResolver: ResourceResolver): ResponseEntity<Any> {
        val result = scriptManager.process(script, form.executionMode, form.customDefinitions, resourceResolver)
        return if (result.isSuccess) {
            ok {
                message = "Script successfully executed"
                "output" set result.entries
            }
        } else {
            internalServerError {
                message = "Errors while executing script"
                "output" set result.entries
                errors = result?.lastError?.messages ?: listOf()
            }
        }
    }
}