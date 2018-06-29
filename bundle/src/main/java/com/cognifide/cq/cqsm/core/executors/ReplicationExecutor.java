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
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.scripts.ScriptContent;
import com.cognifide.cq.cqsm.core.scripts.ScriptStorageImpl;
import com.cognifide.cq.cqsm.core.utils.sling.ResolveCallback;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import com.google.common.collect.ImmutableMap;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;

@Component(
		immediate = true,
		service = {
				JobConsumer.class,
				ResourceChangeListener.class
		},
		property = {
				Property.TOPIC + ReplicationExecutor.JOB_NAME,
				Property.RESOURCE_PATH + ScriptStorageImpl.SCRIPT_PATH,
				Property.CHANGE_TYPE + "ADDED",
				Property.CHANGE_TYPE + "CHANGED",
				Property.DESCRIPTION + "CQSM Replication Event Handler",
				Property.VENDOR
		}
)
public class ReplicationExecutor implements JobConsumer, ResourceChangeListener {

	static final String JOB_NAME = "com/cognifide/cq/cqsm/core/executors/replication/executor";

	private static final Logger LOG = LoggerFactory.getLogger(ReplicationExecutor.class);

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

}
