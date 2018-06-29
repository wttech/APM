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
import com.cognifide.cq.cqsm.core.Property;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.google.common.collect.Maps;

import org.apache.commons.io.FilenameUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

@Component(
		immediate = true,
		service = ScriptReplicator.class,
		property = {
				Property.DESCRIPTION + "CQSM Script Replicator Service",
				Property.VENDOR
		}
)
public class ScriptReplicatorImpl implements ScriptReplicator {

	private static final Logger LOG = LoggerFactory.getLogger(ScriptReplicatorImpl.class);

	private static final String ROOT_PATH = "/etc/cqsm/import/jcr:content";

	private static final String SCRIPT_PATH = ROOT_PATH + "/cqsmImport";

	private static final String REPLICATION_PATH = ROOT_PATH + "/cqsmReplication";

	@Reference
	private Replicator replicator;

	@Reference
	private ScriptManager scriptManager;

	private EventManager eventManager = new EventManager();

	@Override
	public void replicate(Script script, ResourceResolver resolver) throws ExecutionException,
			ReplicationException, PersistenceException {
		eventManager.trigger(Event.BEFORE_REPLICATE, script);

		final List<Script> includes = new LinkedList<>();
		includes.add(script);
		includes.addAll(scriptManager.findIncludes(script, resolver));

		final boolean autocommit = true;
		final Resource includeDir = ResourceUtil
				.getOrCreateResource(resolver, REPLICATION_PATH, JcrConstants.NT_UNSTRUCTURED,
						JcrConstants.NT_UNSTRUCTURED, autocommit);

		for (final Script include : includes) {
			final String path = (script.equals(include) ? SCRIPT_PATH : REPLICATION_PATH) + "/" + FilenameUtils
					.getName(include.getPath());

			LOG.warn("Copying {} to {}", include.getPath(), includeDir.getPath());
			copy(resolver, include.getPath(), includeDir);
			resolver.commit();

			final Session session = resolver.adaptTo(Session.class);
			replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
		}
		resolver.delete(includeDir);
		resolver.commit();
		eventManager.trigger(Event.AFTER_REPLICATE, script);
	}

	private Resource copy(ResourceResolver resolver, String sourcePath, Resource destParent)
			throws PersistenceException {
		Resource source = resolver.getResource(sourcePath);

		Map<String, Object> properties = Maps.newHashMap(source.getValueMap());
		properties.remove(JcrConstants.JCR_UUID);
		Resource dest = resolver.create(destParent, source.getName(), properties);

		if (source.getChild(JcrConstants.JCR_CONTENT) != null) {
			copy(resolver, sourcePath + "/" + JcrConstants.JCR_CONTENT, dest);
		}
		return dest;
	}

}
