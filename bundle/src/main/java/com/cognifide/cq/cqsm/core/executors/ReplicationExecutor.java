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

import com.google.common.collect.ImmutableMap;

import com.cognifide.cq.cqsm.api.scripts.ExecutionMode;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;
import com.cognifide.cq.cqsm.core.scripts.ScriptContent;
import com.cognifide.cq.cqsm.core.scripts.ScriptStorageImpl;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service({JobConsumer.class, ResourceChangeListener.class})
@Component(immediate = true, name = "CQSM Replication Event Handler")
@Properties({
		@Property(name = JobConsumer.PROPERTY_TOPICS, value = ReplicationExecutor.JOB_NAME),
		@Property(name = ResourceChangeListener.PATHS, value = ScriptStorageImpl.SCRIPT_PATH),
		@Property(name = ResourceChangeListener.CHANGES, value = {"ADDED", "CHANGED"})
})
public class ReplicationExecutor extends AbstractExecutor implements JobConsumer, ResourceChangeListener {

	static final String JOB_NAME = "com/cognifide/cq/cqsm/core/executors/replication/executor";

	private static final Logger LOG = LoggerFactory.getLogger(ReplicationExecutor.class);

	@Reference
	private InstanceTypeProvider instanceTypeProvider;

	@Reference
	private JobManager jobManager;

	@Override
	public synchronized JobResult process(Job job) {
		final String searchPath = job.getProperty(SlingConstants.PROPERTY_PATH).toString();
		return SlingHelper.resolveDefault(resolverFactory, resolver -> runReplicated(resolver, searchPath), JobResult.FAILED);
	}

	private JobResult runReplicated(ResourceResolver resolver, String searchPath) {
		JobResult result = JobResult.FAILED;
		final Script script = scriptFinder.find(searchPath, resolver);
		if (script == null) {
			LOG.warn("Replicated script cannot be found by script manager: {}", searchPath);
		} else if (ExecutionMode.ON_DEMAND.equals(script.getExecutionMode()) && script.isPublishRun()) {
			try {
				processScript(script, resolver, "Replication");
				result = JobResult.OK;
			} catch (PersistenceException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return result;
	}

	@Override
	public void onChange(List<ResourceChange> changes) {
		for (ResourceChange change : changes) {
			processChange(change);
		}
	}

	private void processChange(ResourceChange change) {
		String path = change.getPath();
		if (isPublish() && (scriptAdded(change) || scriptChanged(change))) {
			Map<String, Object> eventProperties = ImmutableMap.<String, Object>builder()
					.put(SlingConstants.PROPERTY_PATH, path)
					.build();
			jobManager.addJob(JOB_NAME, eventProperties);
		}
	}

	private boolean isPublish() {
		return !instanceTypeProvider.isOnAuthor();
	}

	private boolean scriptAdded(ResourceChange change) {
		return ResourceChange.ChangeType.ADDED.equals(change.getType()) && scriptVerified(change);
	}

	private boolean scriptVerified(ResourceChange change) {
		Boolean result = false;
		Set<String> addedPropertyNames = change.getAddedPropertyNames();
		if (addedPropertyNames != null) {
			result = addedPropertyNames.contains(ScriptContent.CQSM_VERIFIED);
		}
		return result;
	}

	private boolean scriptChanged(ResourceChange change) {
		return ResourceChange.ChangeType.CHANGED.equals(change.getType()) && (fileReplaced(change));
	}

	private boolean fileReplaced(ResourceChange change) {
		Boolean result = false;
		Set<String> changedPropertyNames = change.getChangedPropertyNames();
		if (changedPropertyNames != null) {
			result = changedPropertyNames.contains(JcrConstants.JCR_UUID);
		}
		return result;
	}

	@Override
	Logger getLogger() {
		return LOG;
	}
}
