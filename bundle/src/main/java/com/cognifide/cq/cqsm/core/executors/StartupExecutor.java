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

import com.cognifide.cq.cqsm.api.scripts.EventListener;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.cognifide.cq.cqsm.core.scripts.ScriptFilters.filterOnStart;

@Component(immediate = true)
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Startup Executor"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
public class StartupExecutor extends AbstractExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(StartupExecutor.class);

	/**
	 * Reference needed for proper event hook up on activation
	 */
	@Reference
	private EventListener eventListener;

	@Activate
	private synchronized void activate() {
		SlingHelper.operateTraced(resolverFactory, this::runOnStartup);
	}

	private void runOnStartup(ResourceResolver resolver) throws PersistenceException {
		final List<Script> scripts = scriptFinder.findAll(filterOnStart(resolver), resolver);
		if (scripts.isEmpty()) {
			if(LOG.isInfoEnabled()) {
				LOG.info("Startup script executor is trying to execute scripts on startup: {}", scripts.size());
				LOG.info(MessagingUtils.describeScripts(scripts));
			}
			for (Script script : scripts) {
				processScript(script, resolver,"Startup");
			}
		} else {
			LOG.info("Startup script executor has nothing to do");
		}
	}

	@Override
	Logger getLogger() {
		return LOG;
	}
}
