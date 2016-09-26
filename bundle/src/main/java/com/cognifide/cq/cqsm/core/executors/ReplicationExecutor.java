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

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.ExecutionMode;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;
import com.cognifide.cq.cqsm.core.scripts.ScriptStorageImpl;
import com.cognifide.cq.cqsm.core.utils.sling.ResolveCallback;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

@Service({ EventHandler.class, JobConsumer.class })
@Component(immediate = true, name = "CQSM Replication Event Handler")
@Properties({ @Property(name = EventConstants.EVENT_TOPIC, value = { SlingConstants.TOPIC_RESOURCE_ADDED,
		SlingConstants.TOPIC_RESOURCE_CHANGED }),
		@Property(name = EventConstants.EVENT_FILTER, value = "(&(path=" + ScriptStorageImpl.SCRIPT_PATH
				+ "*)(resourceAddedAttributes=cqsm:verified))"),
		@Property(name = JobConsumer.PROPERTY_TOPICS, value = ReplicationExecutor.JOB_NAME) })
public class ReplicationExecutor implements EventHandler, JobConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(ReplicationExecutor.class);

	static final String JOB_NAME = "com/cognifide/cq/cqsm/core/executors/replication/executor";

	@Reference
	private ScriptManager scriptManager;

	@Reference
	private ScriptFinder scriptFinder;

	@Reference
	private InstanceTypeProvider instanceTypeProvider;

	@Reference
	private JobManager jobManager;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public synchronized JobResult process(Job job) {
		final String searchPath = job.getProperty(SlingConstants.PROPERTY_PATH).toString();
		return SlingHelper.resolveDefault(resolverFactory, new ResolveCallback<JobResult>() {
			@Override
			public JobResult resolve(ResourceResolver resolver) {
				return runReplicated(resolver, searchPath);
			}
		}, JobResult.FAILED);
	}

	private JobResult runReplicated(ResourceResolver resolver, String searchPath) {
		JobResult result = JobResult.FAILED;
		final Script script = scriptFinder.find(searchPath, resolver);
		if (script == null) {
			LOG.warn("Replicated script cannot be found by script manager: {}", searchPath);
		} else if (ExecutionMode.ON_DEMAND.equals(script.getExecutionMode()) && script.isPublishRun()) {
			try {
				process(script, resolver);
				result = JobResult.OK;
			} catch (PersistenceException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return result;
	}

	private void process(final Script script, ResourceResolver resolver) throws PersistenceException {
		final String scriptPath = script.getPath();

		try {
			scriptManager.process(script, Mode.VALIDATION, resolver);
			if (script.isValid()) {
				final Progress progress = scriptManager.process(script, Mode.AUTOMATIC_RUN, resolver);
				logStatus(scriptPath, progress.isSuccess());
			} else {
				LOG.warn("Schedule executor cannot execute script which is not valid: {}", scriptPath);
			}
		} catch (RepositoryException e) {
			LOG.error("Script cannot be processed because of repository error: {}", scriptPath, e);
		}
	}

	private void logStatus(String scriptPath, boolean success) {
		if (success) {
			LOG.info("Replicated script successfully executed: {}", scriptPath);
		} else {
			LOG.error("Replicated script cannot be executed properly: {}", scriptPath);
		}
	}

	@Override
	public void handleEvent(Event event) {
		if (!instanceTypeProvider.isOnAuthor()) {
			Map<String, Object> eventProperties = new HashMap<>();
			for (String propertyName : event.getPropertyNames()) {
				eventProperties.put(propertyName, event.getProperty(propertyName));
			}
			jobManager.addJob(JOB_NAME, eventProperties);
		}
	}
}
