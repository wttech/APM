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
package com.cognifide.apm.core.jobs;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.endpoints.BackgroundJobParameters;
import com.cognifide.apm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.apm.core.scriptrunnerjob.JobProgressOutput;

@Component(
    immediate = true,
    service = ScriptRunnerJobManager.class,
    property = {
        Property.DESCRIPTION + "CQSM Service for running scripts in background and checking theirs status",
        Property.VENDOR
    }
)
/**
 * @deprecated use {@link com.cognifide.apm.core.services.async.AsyncScriptExecutorImpl} instead
 */
@Deprecated
public class ScriptRunnerJobManagerImpl implements ScriptRunnerJobManager {

  public static final String JOB_SCRIPT_RUN_TOPIC = "script/job/run";

  public static final String SCRIPT_PATH_PROPERTY_NAME = "searchPath";

  public static final String MODE_NAME_PROPERTY_NAME = "modeName";

  public static final String USER_NAME_PROPERTY_NAME = "userName";

  @Reference
  private JobManager jobManager;

  @Reference
  private JobResultsCache jobResultsCache;

  @Override
  public Job scheduleJob(BackgroundJobParameters parameters) {
    final Map<String, Object> props = new HashMap<>();
    props.put(SCRIPT_PATH_PROPERTY_NAME, parameters.getSearchPath());
    props.put(MODE_NAME_PROPERTY_NAME, parameters.getModeName());
    props.put(USER_NAME_PROPERTY_NAME, parameters.getUserName());
    Job job = jobManager.addJob(JOB_SCRIPT_RUN_TOPIC, props);
    jobResultsCache.put(job.getId(), ExecutionSummary.running());
    return job;

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
      return new JobProgressOutput(ScriptRunnerJobStatus.RUNNING);
    } else {
      return getJobProgress(job.getId());
    }
  }

  private JobProgressOutput getJobProgress(String id) {
    ExecutionSummary executionSummary = jobResultsCache.get(id);
    if (executionSummary != null) {
      if (executionSummary.isFinished()) {
        return new JobProgressOutput(
            ScriptRunnerJobStatus.FINISHED, executionSummary.getPath(), executionSummary.getResult().getEntries());
      } else {
        return new JobProgressOutput(ScriptRunnerJobStatus.RUNNING);
      }
    }
    return new JobProgressOutput(ScriptRunnerJobStatus.UNKNOWN);
  }

  private boolean isJobRunning(Job job) {
    return job.getJobState().equals(Job.JobState.ACTIVE) || job.getJobState().equals(Job.JobState.QUEUED);
  }

  private Job findJob(String id) {
    return jobManager.getJobById(id);
  }

}
