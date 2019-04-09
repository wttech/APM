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
package com.cognifide.cq.cqsm.core.scripts;

import com.cognifide.cq.cqsm.api.exceptions.ExecutionException;
import com.cognifide.cq.cqsm.api.scripts.Event;
import com.cognifide.cq.cqsm.api.scripts.EventManager;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.api.scripts.ScriptReplicator;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import java.util.LinkedList;
import java.util.List;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;

@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Script Replicator Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
public class ScriptReplicatorImpl implements ScriptReplicator {

	@Reference
	private Replicator replicator;

	@Reference
	private ScriptManager scriptManager;

	private EventManager eventManager = new EventManager();

	@Override
	public void replicate(Script script, ResourceResolver resolver) throws ExecutionException,
			ReplicationException {

		eventManager.trigger(Event.BEFORE_REPLICATE, script);

		final List<Script> includes = new LinkedList<>();
		includes.add(script);
		includes.addAll(scriptManager.findIncludes(script, resolver));

		final Session session = resolver.adaptTo(Session.class);

		for (final Script include : includes) {
			replicator.replicate(session, ReplicationActionType.ACTIVATE, include.getPath());
		}

		eventManager.trigger(Event.AFTER_REPLICATE, script);
	}
}
