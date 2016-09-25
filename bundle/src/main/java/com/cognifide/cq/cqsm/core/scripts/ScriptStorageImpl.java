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

import com.google.common.collect.Lists;

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.scripts.Event;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.day.cq.commons.jcr.JcrConstants;

import org.apache.commons.io.FilenameUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;

@Component
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "Storage accessor for scripts"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})

public class ScriptStorageImpl implements ScriptStorage {

	private static final Logger LOG = LoggerFactory.getLogger(ScriptStorageImpl.class);

	private static final String ROOT_PATH = "/etc/cqsm/import/jcr:content";

	public static final String SCRIPT_PATH = ROOT_PATH + "/cqsmImport";

	private static final Charset SCRIPT_ENCODING = StandardCharsets.UTF_8;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC)
	volatile private ScriptManager scriptManager;

	@Reference
	private ScriptFinder scriptFinder;

	@Override
	public void remove(final Script script, ResourceResolver resolver) throws RepositoryException {
		scriptManager.getEventManager().trigger(Event.BEFORE_REMOVE, script);
		final Session session = resolver.adaptTo(Session.class);
		final String path = script.getPath();
		if (path != null) {
			session.removeItem(path);
			session.save();
		}
	}

	@Override
	public Script save(String fileName, InputStream input, boolean overwrite, ResourceResolver resolver)
			throws RepositoryException, PersistenceException {
		final Script script = saveScript(resolver, fileName, input, overwrite);
		scriptManager.process(script, Mode.VALIDATION, resolver);
		scriptManager.getEventManager().trigger(Event.AFTER_SAVE, script);
		return script;
	}

	@Override
	public String getSavePath() {
		return SCRIPT_PATH;
	}

	@Override
	public List<Script> saveAll(Map<String, InputStream> files, boolean overwrite, ResourceResolver resolver)
			throws RepositoryException, PersistenceException {
		final List<Script> scripts = Lists.newArrayList();
		for (Map.Entry<String, InputStream> entry : files.entrySet()) {
			scripts.add(saveScript(resolver, entry.getKey(), entry.getValue(), overwrite));
		}
		for (Script script : scripts) {
			scriptManager.process(script, Mode.VALIDATION, resolver);
			scriptManager.getEventManager().trigger(Event.AFTER_SAVE, script);
		}
		return scripts;
	}

	private Script saveScript(ResourceResolver resolver, final String fileName, final InputStream input,
			final boolean overwrite) {
		Script result = null;
		final Session session = resolver.adaptTo(Session.class);
		final ValueFactory valueFactory;
		try {
			valueFactory = session.getValueFactory();
			final Binary binary = valueFactory.createBinary(input);
			final Node saveNode = session.getNode(getSavePath());

			final Node fileNode, contentNode;
			if (overwrite && saveNode.hasNode(fileName)) {
				fileNode = saveNode.getNode(fileName);
				contentNode = fileNode.getNode(JcrConstants.JCR_CONTENT);
			} else {
				fileNode = saveNode.addNode(generateFileName(fileName, saveNode), JcrConstants.NT_FILE);
				contentNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
			}

			contentNode.setProperty(JcrConstants.JCR_ENCODING, SCRIPT_ENCODING.name());
			contentNode.setProperty(JcrConstants.JCR_DATA, binary);
			session.save();
			result = scriptFinder.find(fileNode.getPath(), resolver);
		} catch (RepositoryException e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	private String generateFileName(String fileName, Node saveNode) throws RepositoryException {
		String baseName = FilenameUtils.getBaseName(fileName);
		int num = 1;
		do {
			fileName = baseName + ((num > 1) ? ("-" + num) : "") + Cqsm.FILE_EXT;
			num++;
		} while (saveNode.hasNode(fileName));

		return fileName;
	}

}
