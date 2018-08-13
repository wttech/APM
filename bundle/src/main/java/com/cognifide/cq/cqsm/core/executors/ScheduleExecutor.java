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

import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;

import java.util.Date;
import java.util.List;

import static com.cognifide.cq.cqsm.core.scripts.ScriptFilters.filterOnSchedule;

@Component(immediate = true)
@Service(Runnable.class)
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Schedule Executor"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME),
		@Property(name = "scheduler.expression", value = "0 * * * * ?")})
public class ScheduleExecutor extends AbstractExecutor implements Runnable {

	@Override
	public synchronized void run() {
		SlingHelper.operateTraced(resolverFactory, this::runScheduled);
	}

	private void runScheduled(ResourceResolver resolver) throws PersistenceException {
		final List<Script> scripts = scriptFinder.findAll(filterOnSchedule(new Date()), resolver);
		if (scripts.isEmpty()) {
			return;
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("Schedule executor is trying to execute script(s): %d", scripts.size()));
			logger.info(MessagingUtils.describeScripts(scripts));
		}
		for (Script script : scripts) {
			processScript(script, resolver, "Schedule");
		}
	}
}
