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
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.core.utils.sling.OperateCallback;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import javax.jcr.RepositoryException;

import static com.cognifide.cq.cqsm.core.scripts.ScriptFilters.filterOnSchedule;

@Component(immediate = true)
@Service(Runnable.class)
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Schedule Executor"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME),
		@Property(name = "scheduler.expression", value = "0 * * * * ?")})
public class ScheduleExecutor implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduleExecutor.class);

	@Reference
	private ScriptManager scriptManager;

	@Reference
	private ScriptFinder scriptFinder;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public synchronized void run() {
		SlingHelper.operateTraced(resolverFactory, new OperateCallback() {
			@Override
			public void operate(ResourceResolver resolver) throws Exception {
				runScheduled(resolver);
			}
		});
	}

	private void runScheduled(ResourceResolver resolver) throws PersistenceException {
		final List<Script> scripts = scriptFinder.findAll(filterOnSchedule(new Date()), resolver);
		if (scripts.size() == 0) {
			return;
		}

		LOG.info("Schedule executor is trying to execute script(s): " + scripts.size());
		LOG.info(MessagingUtils.describeScripts(scripts));
		for (Script script : scripts) {
			processScript(script, resolver);
		}
	}

	private void processScript(Script script, ResourceResolver resolver) throws PersistenceException {
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
			LOG.info("Scheduled script successfully executed: {}", scriptPath);
		} else {
			LOG.error("Scheduled script cannot be executed properly: {}", scriptPath);
		}
	}
}
