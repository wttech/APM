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

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.cq.cqsm.api.history.History;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.cq.cqsm.core.utils.sling.ResolveCallback;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
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
        Property.TOPIC + ScriptRunnerJobManagerImpl.JOB_SCRIPT_RUN_TOPIC
    }
)
public class ScriptRunnerJobConsumer implements JobConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(ScriptRunnerJobConsumer.class);

  @Reference
  private History history;

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
    final String id = job.getId();
    final ExecutionMode mode = getMode(job);
    final String userId = getUserId(job);
    return resolveDefault(resolverFactory, userId, (ResolveCallback<JobResult>) resolver -> {
      JobResult result = JobResult.FAILED;
      final Script script = getScript(job, resolver);
      if (script != null && mode != null) {
        try {
          ExecutionResult executionResult = scriptManager.process(script, mode, resolver);
          String summaryPath = getSummaryPath(script, mode);
          jobResultsCache.put(id, ExecutionSummary.finished(executionResult, summaryPath));
          result = JobResult.OK;
        } catch (RepositoryException | PersistenceException e) {
          LOG.error("Script manager failed to process script", e);
          result = JobResult.FAILED;
        }
      }
      return result;
    }, JobResult.FAILED);
  }

  private String getSummaryPath(Script script, ExecutionMode mode) {
    return resolveDefault(resolverFactory, (ResolveCallback<String>) resolver -> {
      if (mode == ExecutionMode.DRY_RUN) {
        return history.findScriptHistory(resolver, script).getLastLocalDryRunPath();
      } else if (mode == ExecutionMode.RUN) {
        return history.findScriptHistory(resolver, script).getLastLocalRunPath();
      }
      return StringUtils.EMPTY;
    }, StringUtils.EMPTY);
  }

  private ExecutionMode getMode(Job job) {
    ExecutionMode result = null;
    String modeName = (String) job.getProperty(ScriptRunnerJobManagerImpl.MODE_NAME_PROPERTY_NAME);
    if (StringUtils.isNotBlank(modeName)) {
      result = StringUtils.isEmpty(modeName) ? ExecutionMode.DRY_RUN : ExecutionMode.valueOf(modeName.toUpperCase());
    } else {
      LOG.error("Mode is null");
    }
    return result;
  }

  private Script getScript(Job job, ResourceResolver resolver) {
    String scriptSearchPath = (String) job
        .getProperty(ScriptRunnerJobManagerImpl.SCRIPT_PATH_PROPERTY_NAME);
    if (StringUtils.isNotBlank(scriptSearchPath)) {
      final Script script = scriptFinder.find(scriptSearchPath, resolver);
      if (script == null) {
        LOG.error("Script not found: %s", scriptSearchPath);
        return null;
      }
      return script;
    } else {
      LOG.error("Script search path is blank");
      return null;
    }
  }

  private String getUserId(Job job) {
    return job.getProperty(ScriptRunnerJobManagerImpl.USER_NAME_PROPERTY_NAME, String.class);
  }
}