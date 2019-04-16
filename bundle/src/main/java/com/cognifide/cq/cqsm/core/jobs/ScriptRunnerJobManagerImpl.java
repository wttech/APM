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

import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scriptrunnerjob.JobProgressOutput;
import com.cognifide.cq.cqsm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.cq.cqsm.core.servlets.BackgroundJobParameters;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;

@Component(immediate = true)
@Service
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Service for running scripts in background and checking theirs status"),
    @Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd")})
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
    return jobManager.addJob(JOB_SCRIPT_RUN_TOPIC, props);

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
