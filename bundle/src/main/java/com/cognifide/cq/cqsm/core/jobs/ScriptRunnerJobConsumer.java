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

import static com.cognifide.cq.cqsm.core.utils.sling.SlingHelper.resolveDefault;

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.cq.cqsm.core.servlets.run.ScriptRunParameters;
import com.cognifide.cq.cqsm.core.utils.sling.ResolveCallback;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
		immediate = true,
		service = JobConsumer.class,
		property = {
				Property.TOPIC + ScriptRunParameters.JOB_SCRIPT_RUN_TOPIC
		}
)
public class ScriptRunnerJobConsumer implements JobConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(ScriptRunnerJobConsumer.class);

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private JobResultsCache jobResultsCache;

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Override
  public JobResult process(final Job job) {
    LOG.info("Script runner job consumer started");

    ScriptRunParameters parameters = fromJob(job);

    return resolveDefault(resolverFactory, parameters.getUserName(), (ResolveCallback<JobResult>) resolver -> {
      JobResult result;
      final Mode mode = Mode.fromString(parameters.getModeName(),Mode.DRY_RUN);
      final Script script = scriptFinder.find(parameters.getSearchPath(), resolver);
      try {
        Progress progressLogger = scriptManager.process(script, mode, resolver);
        String summaryPath = getSummaryPath(script, mode);
        jobResultsCache.put(job.getId(), new ExecutionSummary(progressLogger, summaryPath));
        result = JobResult.OK;
      } catch (RepositoryException | PersistenceException e) {
        LOG.error("Script manager failed to process script", e);
        result = JobResult.FAILED;
      }

      return result;
    }, JobResult.FAILED);
  }

  private String getSummaryPath(Script script, Mode mode) {
    if (mode == Mode.DRY_RUN) {
      return script.getDryRunSummary();
    } else if (mode == Mode.RUN) {
      return script.getRunSummary();
    }
    return StringUtils.EMPTY;
  }

  private ScriptRunParameters fromJob(Job job) {
    String searchPath = job
            .getProperty(ScriptRunParameters.SCRIPT_PATH_PROPERTY_NAME, String.class);
    String modeName = job.getProperty(ScriptRunParameters.MODE_NAME_PROPERTY_NAME, String.class);
    String userName = job.getProperty(ScriptRunParameters.USER_NAME_PROPERTY_NAME, String.class);
    return new ScriptRunParameters(searchPath, modeName, userName);
  }

}