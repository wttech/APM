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
package com.cognifide.apm.core.services.async

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ExecutionMode
import com.cognifide.apm.api.status.Status
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.jobs.JobResultsCache
import com.cognifide.apm.core.jobs.JobResultsCache.ExecutionSummary
import com.cognifide.apm.core.jobs.ScriptRunnerJobConsumer
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import java.util.*
import kotlin.concurrent.thread

@Component(
        immediate = true,
        service = [AsyncScriptExecutor::class],
        property = [
            Property.DESCRIPTION + "APM Service for executing scripts in background and checking theirs status",
            Property.VENDOR
        ]
)
class AsyncScriptExecutorImpl : AsyncScriptExecutor {

    @Reference
    @Transient
    private lateinit var scriptRunnerJobConsumer: ScriptRunnerJobConsumer

    @Reference
    @Transient
    private lateinit var jobResultsCache: JobResultsCache

    override fun process(script: Script, executionMode: ExecutionMode, customDefinitions: Map<String, String>, resourceResolver: ResourceResolver): String {
        val id = UUID.randomUUID().toString()
        val properties = mutableMapOf<String, Any>()
        properties[ID] = id
        properties[SCRIPT_PATH] = script.path
        properties[EXECUTION_MODE] = executionMode.toString()
        properties[USER_ID] = resourceResolver.userID!!
        properties[DEFINITIONS] = customDefinitions
        jobResultsCache.put(id, ExecutionSummary.running())
        thread(start = true) {
            scriptRunnerJobConsumer.process(properties)
        }
        return id
    }

    override fun checkStatus(id: String): ExecutionStatus {
        val executionSummary = jobResultsCache[id]
        return when {
            executionSummary == null -> UnknownExecution()
            executionSummary.isFinished -> finishedExecution(executionSummary)
            else -> RunningExecution()
        }
    }

    private fun finishedExecution(executionSummary: ExecutionSummary): ExecutionStatus {
        val entries = executionSummary.result.entries
        val errorEntry = entries.findLast { it.status == Status.ERROR }
        return if (errorEntry != null) {
            FinishedFailedExecution(executionSummary.path, entries, errorEntry)
        } else {
            FinishedSuccessfulExecution(executionSummary.path, entries)
        }
    }

    companion object {
        const val SCRIPT_PATH = "searchPath"
        const val EXECUTION_MODE = "modeName"
        const val USER_ID = "userName"
        const val DEFINITIONS = "definitions"
        const val ID = "id"
    }

}