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

import com.cognifide.cq.cqsm.api.scripts.ExecutionMode;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;

@Service(JobConsumer.class)
@Component(immediate = true, name = "CQSM Replication Event Handler")
@Properties({
		@Property(name = JobConsumer.PROPERTY_TOPICS, value = ReplicationExecutor.JOB_NAME)
})
public class ReplicationExecutor extends AbstractExecutor implements JobConsumer {

	public static final String JOB_NAME = "com/cognifide/cq/cqsm/core/executors/replication/executor";

	@Override
	public synchronized JobResult process(Job job) {
		JobResult result = JobResult.FAILED;
		final String searchPath = job.getProperty(SlingConstants.PROPERTY_PATH).toString();
		final Script script = getScript(searchPath);
		if (script != null) {
			final String userId = getUserId(script);
			result = SlingHelper.resolveDefault(resolverFactory, userId, resolver -> runReplicated(resolver, script), JobResult.FAILED);
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

		if (ExecutionMode.ON_DEMAND.equals(script.getExecutionMode()) && script.isPublishRun()) {
			try {
				processScript(script, resolver, ExecutorType.REPLICATION);
				result = JobResult.OK;
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}

}
