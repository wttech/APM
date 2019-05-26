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
package com.cognifide.cq.cqsm.core.jobs;

import static com.cognifide.cq.cqsm.core.jobs.ScriptRunnerJobStatus.FINISHED;
import static com.cognifide.cq.cqsm.core.jobs.ScriptRunnerJobStatus.RUNNING;
import static com.cognifide.cq.cqsm.core.jobs.ScriptRunnerJobStatus.UNKNOWN;

import com.cognifide.cq.cqsm.api.scriptrunnerjob.JobProgressOutput;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.cq.cqsm.core.servlets.run.ScriptRunParameters;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = ScriptRunnerJobManager.class,
    property = {
        Property.DESCRIPTION + "CQSM Service for running scripts in background and checking theirs status",
        Property.VENDOR
    }
)
public class ScriptRunnerJobManagerImpl implements ScriptRunnerJobManager {

  @Reference
  private JobManager jobManager;

  @Reference
  private JobResultsCache jobResultsCache;

  @Override
  public Job scheduleJob(ScriptRunParameters parameters) {
    return jobManager.addJob(ScriptRunParameters.JOB_SCRIPT_RUN_TOPIC,parameters.asMap());
  }

  @Override
  public JobProgressOutput checkJobStatus(String id) {
    Job job = findJob(id);
    if (job != null) {
      return getJobProgressIfFinished(job);
    } else {
      return getJobProgress(id);
    }

  }

  private JobProgressOutput getJobProgressIfFinished(Job job) {
    if (isJobRunning(job)) {
      return new JobProgressOutput(RUNNING);
    } else {
      return getJobProgress(job.getId());
    }
  }

  private JobProgressOutput getJobProgress(String id) {
    ExecutionSummary executionSummary = jobResultsCache.get(id);
    if (executionSummary != null) {
      return new JobProgressOutput(FINISHED, executionSummary.getPath(), executionSummary.getProgress().getEntries());
    }
    return new JobProgressOutput(UNKNOWN);
  }

  private boolean isJobRunning(Job job) {
    return job.getJobState().equals(Job.JobState.ACTIVE) || job.getJobState().equals(Job.JobState.QUEUED);
  }

  private Job findJob(String id) {
    return jobManager.getJobById(id);
  }

}
