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

import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.core.executors.ReplicationExecutor;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationEvent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Arrays;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(immediate = true)
@Service
@Property(name = EventConstants.EVENT_TOPIC, value = ReplicationEvent.EVENT_TOPIC)
public class ReplicationConsumer implements EventHandler {

	@Reference
	private JobManager jobManager;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private ScriptFinder scriptFinder;

	@Override
	public void handleEvent(Event event) {
		ReplicationAction action = ReplicationEvent.fromEvent(event).getReplicationAction();
		Arrays.stream(action.getPaths())
				.filter(path -> path.startsWith("/conf/apm/scripts"))
				.forEach(this::run);
	}

	public void run(String scriptPath) {
		String userId = getUserId(scriptPath);

		Builder<String, Object> builder = ImmutableMap.builder();

		builder.put(SlingConstants.PROPERTY_PATH, scriptPath);

		if (userId != null) {
			builder.put(SlingConstants.PROPERTY_USERID, userId);
		}

		jobManager.addJob(ReplicationExecutor.JOB_NAME, builder.build());
	}

	private String getUserId(String scriptPath) {
		return SlingHelper.resolveDefault(resolverFactory, resolver -> getReplicatedBy(scriptPath, resolver), null);
	}

	private String getReplicatedBy(String scriptPath, ResourceResolver resolver) {
		return scriptFinder.find(scriptPath, resolver).getReplicatedBy();
	}

}
