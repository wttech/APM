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
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.apm.api.status.Status
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.history.History
import com.cognifide.apm.core.jobs.JobResultsCache
import com.cognifide.apm.core.jobs.JobResultsCache.ExecutionSummary
import com.cognifide.apm.core.services.ResourceResolverProvider
import com.cognifide.apm.core.utils.sling.SlingHelper
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import java.util.*

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
    private lateinit var jobResultsCache: JobResultsCache

    @Reference
    @Transient
    private lateinit var resolverProvider: ResourceResolverProvider

    @Reference
    @Transient
    private lateinit var scriptManager: ScriptManager

    @Reference
    @Transient
    private lateinit var history: History

    override fun process(script: Script, executionMode: ExecutionMode, customDefinitions: Map<String, String>, resourceResolver: ResourceResolver): String {
        val userId = resourceResolver.userID!!
        val id = UUID.randomUUID().toString()
        jobResultsCache.put(id, ExecutionSummary.running())
        Thread({
            SlingHelper.operateTraced(resolverProvider, userId) {
                val executionResult = scriptManager.process(script, executionMode, customDefinitions, it)
                val summaryPath = getSummaryPath(script, executionMode)
                jobResultsCache.put(id, ExecutionSummary.finished(executionResult, summaryPath))
            }
        }, THREAD).start()
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

    private fun getSummaryPath(script: Script, mode: ExecutionMode): String {
        return SlingHelper.resolveDefault(resolverProvider, {
            when (mode) {
                ExecutionMode.DRY_RUN -> history.findScriptHistory(it, script).lastLocalDryRunPath
                ExecutionMode.RUN -> history.findScriptHistory(it, script).lastLocalRunPath
                else -> StringUtils.EMPTY
            }
        }, StringUtils.EMPTY)
    }

    companion object {
        const val THREAD = "apm-async"
    }
}