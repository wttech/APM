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

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.core.utils.sling.ResolveCallback;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

@Component(immediate = true)
@Service
@Property(name = JobConsumer.PROPERTY_TOPICS, value = ScriptRunnerJobManagerImpl.JOB_SCRIPT_RUN_TOPIC)
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
		final String id = job.getId();
		final Mode mode = getMode(job);
		final String userId = getUserId(job);
		return SlingHelper.resolveDefault(resolverFactory, userId, new ResolveCallback<JobResult>() {
			@Override
			public JobResult resolve(ResourceResolver resolver) {
				JobResult result = JobResult.FAILED;
				final Script script = getScript(job, resolver);
				if (script != null && mode != null) {
					try {
						final Progress progressLogger = scriptManager.process(script, mode, resolver);
						jobResultsCache.put(id, progressLogger);
						result = JobResult.OK;
					} catch (RepositoryException | PersistenceException e) {
						LOG.error("Script manager failed to process script", e);
						result = JobResult.FAILED;
					}
				}
				return result;
			}
		}, JobResult.FAILED);
	}

	private Mode getMode(Job job) {
		Mode result = null;
		String modeName = (String) job.getProperty(ScriptRunnerJobManagerImpl.MODE_NAME_PROPERTY_NAME);
		if (StringUtils.isNotBlank(modeName)) {
			result = StringUtils.isEmpty(modeName) ? Mode.DRY_RUN : Mode.valueOf(modeName.toUpperCase());
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