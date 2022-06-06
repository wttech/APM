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


import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.services.async.AsyncScriptExecutorImpl;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = ScriptRunnerJobConsumer.class
)
public class ScriptRunnerJobConsumer {

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
  private ResourceResolverProvider resolverProvider;

  public void process(Map<String, Object> properties) {
    LOG.info("Script runner properties consumer started");
    final String id = (String) properties.get(AsyncScriptExecutorImpl.ID);
    final ExecutionMode mode = getMode(properties);
    final String userId = getUserId(properties);
    SlingHelper.operateTraced(resolverProvider, userId, resolver -> {
      final Script script = getScript(properties, resolver);
      if (script != null && mode != null) {
        try {
          ExecutionResult executionResult = scriptManager.process(script, mode, getDefinitions(properties), resolver);
          String summaryPath = getSummaryPath(resolver, script, mode);
          jobResultsCache.put(id, ExecutionSummary.finished(executionResult, summaryPath));
        } catch (RepositoryException | PersistenceException e) {
          LOG.error("Script manager failed to process script", e);
        }
      }
    });
  }

  private String getSummaryPath(ResourceResolver resolver, Script script, ExecutionMode mode) {
    if (mode == ExecutionMode.DRY_RUN) {
      return history.findScriptHistory(resolver, script).getLastLocalDryRunPath();
    } else if (mode == ExecutionMode.RUN) {
      return history.findScriptHistory(resolver, script).getLastLocalRunPath();
    }
    return StringUtils.EMPTY;
  }

  private ExecutionMode getMode(Map<String, Object> properties) {
    ExecutionMode result = null;
    String modeName = (String) properties.get(AsyncScriptExecutorImpl.EXECUTION_MODE);
    if (StringUtils.isNotBlank(modeName)) {
      result = StringUtils.isEmpty(modeName) ? ExecutionMode.DRY_RUN : ExecutionMode.valueOf(modeName.toUpperCase());
    } else {
      LOG.error("Mode is null");
    }
    return result;
  }

  private Map<String, String> getDefinitions(Map<String, Object> properties) {
    HashMap<String, String> definitions = (HashMap<String, String>) properties.get(AsyncScriptExecutorImpl.DEFINITIONS);
    if (definitions == null) {
      definitions = new HashMap<>();
    }
    return definitions;
  }

  private Script getScript(Map<String, Object> properties, ResourceResolver resolver) {
    String scriptSearchPath = (String) properties.get(AsyncScriptExecutorImpl.SCRIPT_PATH);
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

  private String getUserId(Map<String, Object> properties) {
    return (String) properties.get(AsyncScriptExecutorImpl.USER_ID);
  }
}