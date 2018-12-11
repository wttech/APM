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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.io.FilenameUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final String ROOT_PATH = "/conf/apm";

	private static final String SCRIPT_PATH = ROOT_PATH + "/scripts";

	private static final String REPLICATION_PATH = ROOT_PATH + "/replication";

	@Reference
	private Replicator replicator;

	@Reference
	private ScriptManager scriptManager;

	private EventManager eventManager = new EventManager();

	@Override
	public void replicate(Script script, ResourceResolver resolver) throws ExecutionException,
			ReplicationException, PersistenceException, RepositoryException {

		eventManager.trigger(Event.BEFORE_REPLICATE, script);

		final List<Script> includes = new LinkedList<>();
		includes.add(script);
		includes.addAll(scriptManager.findIncludes(script, resolver));

		final Session session = resolver.adaptTo(Session.class);

		createReplicationFolder(session);

		for (final Script include : includes) {
			final String path =
					(script.equals(include) ? SCRIPT_PATH : REPLICATION_PATH) + "/" + FilenameUtils
							.getName(include.getPath());

			LOG.warn("Copying {} to {}", include.getPath(), REPLICATION_PATH);
			copy(resolver, include.getPath(), resolver.getResource(REPLICATION_PATH));
			resolver.commit();

			replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
		}
		Resource replicationResource = resolver.getResource(REPLICATION_PATH);
		if (replicationResource != null) {
			resolver.delete(replicationResource);
			resolver.commit();
		}

		eventManager.trigger(Event.AFTER_REPLICATE, script);
	}

	private void createReplicationFolder(Session session)
			throws RepositoryException {
		if (session != null) {
			Node node = session.getNode(ROOT_PATH);
			if (node != null) {
				JcrUtils.getOrAddFolder(node, "replication");
				session.save();
			}
		}
	}

	private Resource copy(ResourceResolver resolver, String sourcePath, Resource destParent)
			throws PersistenceException {
		Resource dest = null;
		Resource source = resolver.getResource(sourcePath);

		if (source != null) {
			Map<String, Object> properties = Maps.newHashMap(source.getValueMap());
			properties.remove(JcrConstants.JCR_UUID);

			if (destParent != null) {
				dest = resolver.create(destParent, source.getName(), properties);
			}

			if (source.getChild(JcrConstants.JCR_CONTENT) != null) {
				copy(resolver, sourcePath + "/" + JcrConstants.JCR_CONTENT, dest);
			}
		}
		return dest;
	}

}
