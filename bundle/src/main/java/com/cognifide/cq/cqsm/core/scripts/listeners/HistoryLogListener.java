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
package com.cognifide.cq.cqsm.core.scripts.listeners;

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.core.history.History;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.Event;
import com.cognifide.cq.cqsm.api.scripts.EventListener;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;
import com.cognifide.cq.cqsm.core.Cqsm;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

@Component
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM History Log Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
public class HistoryLogListener implements EventListener {

	@Reference
	private ScriptManager scriptManager;

	@Reference
	private InstanceTypeProvider instanceTypeProvider;

	@Reference
	private History history;

	@Activate
	private void activate() {
		if (instanceTypeProvider.isOnAuthor()) {
			scriptManager.getEventManager().addListener(Event.AFTER_EXECUTE, this);
		}
	}

	@Override
	public void handle(Script script, Mode mode, Progress progress) {
		if (mode.isRun()) {
			history.log(script, mode, progress);
		}
	}
}
