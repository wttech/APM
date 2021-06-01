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

import static com.cognifide.apm.core.utils.sling.SlingHelper.resolveDefault;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.apm.core.utils.sling.ResolveCallback;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = JobConsumer.class,
    property = {
        Property.TOPIC + ScriptRunnerJobManagerImpl.JOB_SCRIPT_RUN_TOPIC
    }
)
@Slf4j
public class ScriptRunnerJobConsumer implements JobConsumer {

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
    log.info("Script runner job consumer started");
    final String id = job.getId();
    final ExecutionMode mode = getMode(job);
    final String userId = getUserId(job);
    return resolveDefault(resolverFactory, userId, (ResolveCallback<JobResult>) resolver -> {
      JobResult result = JobResult.FAILED;
      final Script script = getScript(job, resolver);
      if (script != null && mode != null) {
        try {
          ExecutionResult executionResult = scriptManager.process(script, mode, getDefinitions(job), resolver);
          String summaryPath = getSummaryPath(script, mode);
          jobResultsCache.put(id, ExecutionSummary.finished(executionResult, summaryPath));
          result = JobResult.OK;
        } catch (RepositoryException | PersistenceException e) {
          log.error("Script manager failed to process script", e);
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
      log.error("Mode is null");
    }
    return result;
  }

  private Map<String, String> getDefinitions(Job job) {
    HashMap<String, String> definitions = (HashMap<String, String>) job.getProperty("definitions");
    if (definitions == null) {
      definitions = new HashMap<>();
    }
    return definitions;
  }

  private Script getScript(Job job, ResourceResolver resolver) {
    String scriptSearchPath = (String) job
        .getProperty(ScriptRunnerJobManagerImpl.SCRIPT_PATH_PROPERTY_NAME);
    if (StringUtils.isNotBlank(scriptSearchPath)) {
      final Script script = scriptFinder.find(scriptSearchPath, resolver);
      if (script == null) {
        log.error("Script not found: {}", scriptSearchPath);
        return null;
      }
      return script;
    } else {
      log.error("Script search path is blank");
      return null;
    }
  }

  private String getUserId(Job job) {
    return job.getProperty(ScriptRunnerJobManagerImpl.USER_NAME_PROPERTY_NAME, String.class);
  }
}