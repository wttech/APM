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
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.event.jobs.Job
import org.apache.sling.event.jobs.JobManager
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

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
    private lateinit var jobManager: JobManager

    @Reference
    @Transient
    private lateinit var jobResultsCache: JobResultsCache

    override fun process(script: Script, executionMode: ExecutionMode, customDefinitions: Map<String, String>, resourceResolver: ResourceResolver): String {
        val properties = mutableMapOf<String, Any>()
        properties[SCRIPT_PATH] = script.path
        properties[EXECUTION_MODE] = executionMode.toString()
        properties[USER_ID] = resourceResolver.userID!!
        properties[DEFINITIONS] = customDefinitions
        val job = jobManager.addJob(TOPIC, properties)
        jobResultsCache.putIfAbsent(job.id, ExecutionSummary.running())
        return job.id
    }

    override fun checkStatus(id: String): ExecutionStatus {
        val executionSummary = jobResultsCache[id]
        val job = findJob(id)
        return when {
            isJobRunning(job) -> RunningExecution()
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

    private fun isJobRunning(job: Job?): Boolean {
        return job != null && (job.jobState == Job.JobState.ACTIVE || job.jobState == Job.JobState.QUEUED)
    }

    private fun findJob(id: String): Job? {
        return jobManager.getJobById(id)
    }

    companion object {
        const val TOPIC = "script/job/run"
        const val SCRIPT_PATH = "searchPath"
        const val EXECUTION_MODE = "modeName"
        const val USER_ID = "userName"
        const val DEFINITIONS = "definitions"
    }
}