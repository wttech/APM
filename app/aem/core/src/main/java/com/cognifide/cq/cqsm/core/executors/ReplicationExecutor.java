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
package com.cognifide.cq.cqsm.core.executors;

import com.cognifide.apm.api.scripts.LaunchMode;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = {
        JobConsumer.class
    },
    property = {
        Property.TOPIC + ReplicationExecutor.JOB_NAME,
        Property.VENDOR
    }
)
public class ReplicationExecutor extends AbstractExecutor implements JobConsumer {

  public static final String JOB_NAME = "com/cognifide/cq/cqsm/core/executors/replication/executor";

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Override
  public synchronized JobResult process(Job job) {
    JobResult result = JobResult.FAILED;
    final String searchPath = job.getProperty(SlingConstants.PROPERTY_PATH).toString();
    final Script script = getScript(searchPath);
    if (script != null) {
      final String userId = getUserId(script);
      result = SlingHelper
          .resolveDefault(resolverFactory, userId, resolver -> runReplicated(resolver, script), JobResult.FAILED);
    } else {
      logger.warn("Replicated script cannot be found by script manager: {}", searchPath);
    }
    return result;
  }

  private Script getScript(String searchPath) {
    return SlingHelper.resolveDefault(resolverFactory, resolver -> scriptFinder.find(searchPath, resolver), null);
  }

  private String getUserId(Script script) {
    return script.getReplicatedBy();
  }

  private JobResult runReplicated(ResourceResolver resolver, Script script) {
    JobResult result = JobResult.FAILED;

    if (LaunchMode.ON_DEMAND.equals(script.getLaunchMode()) && script.isPublishRun()) {
      try {
        processScript(script, resolver, ExecutorType.REPLICATION);
        result = JobResult.OK;
      } catch (PersistenceException e) {
        logger.error(e.getMessage(), e);
      }
    }
    return result;
  }

  @Override
  public ScriptManager getScriptManager() {
    return scriptManager;
  }
}
