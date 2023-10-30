/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
package com.cognifide.apm.core.services.version;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.grammar.ReferenceFinder;
import com.cognifide.apm.core.grammar.ScriptExecutionException;
import com.cognifide.apm.core.grammar.datasource.DataSourceInvoker;
import com.cognifide.apm.core.scripts.MutableScriptWrapper;
import com.cognifide.apm.core.scripts.ScriptNode;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.crx.JcrConstants;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    property = {
        Property.DESCRIPTION + "APM Version Service",
        Property.VENDOR
    }
)
public class VersionServiceImpl implements VersionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(VersionServiceImpl.class);

  private final static String VERSIONS_ROOT = "/var/apm/versions";

  private final static String SCRIPT_NODE_NAME = "script";

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private DataSourceInvoker dataSourceInvoker;

  @Override
  public ScriptVersion getScriptVersion(ResourceResolver resolver, Script script) {
    String scriptVersionPath = getScriptVersionPath(script);
    return Optional.ofNullable(resolver.getResource(scriptVersionPath))
        .map(resource -> resource.adaptTo(ScriptVersionModel.class))
        .orElse(new ScriptVersionModel(script.getPath(), null));
  }

  @Override
  public String getVersionPath(Script script) {
    return VERSIONS_ROOT + "/" + normalizedPath(script) + "/" + script.getChecksum() + "/" + SCRIPT_NODE_NAME;
  }

  @Override
  public String countChecksum(Iterable<Script> root) {
    String checksums = StreamSupport.stream(root.spliterator(), false)
        .map(Script::getData)
        .map(DigestUtils::md5Hex)
        .collect(Collectors.joining());
    return DigestUtils.md5Hex(checksums);
  }

  @Override
  public void updateVersionIfNeeded(ResourceResolver resolver, Script... scripts) {
    ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resolver, dataSourceInvoker);
    for (Script script : scripts) {
      try {
        List<Script> subtree = referenceFinder.findReferences(script);
        String checksum = countChecksum(subtree);
        ScriptVersion scriptVersion = getScriptVersion(resolver, script);
        if (!StringUtils.equals(checksum, script.getChecksum())) {
          MutableScriptWrapper mutableScriptWrapper = new MutableScriptWrapper(script);
          mutableScriptWrapper.setChecksum(checksum);
        }
        if (!StringUtils.equals(checksum, scriptVersion.getLastChecksum())) {
          createVersion(resolver, script);
        }
      } catch (ScriptExecutionException | PersistenceException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }

  private void createVersion(ResourceResolver resolver, Script script) {
    try {
      Session session = resolver.adaptTo(Session.class);
      Node scriptNode = createScriptNode(script, session);
      Node versionNode = createVersionNode(scriptNode, script, session);
      copyScriptContent(versionNode, script, session);
      session.save();
      resolver.commit();
    } catch (Exception e) {
      LOGGER.error("Issues with saving to repository while logging script execution", e);
    }
  }

  private Node createScriptNode(Script script, Session session) throws RepositoryException {
    String path = getScriptVersionPath(script);
    Node scriptHistory = JcrUtils.getOrCreateByPath(path, "sling:OrderedFolder", JcrConstants.NT_UNSTRUCTURED, session, true);
    scriptHistory.setProperty("scriptPath", script.getPath());
    scriptHistory.setProperty("lastChecksum", script.getChecksum());
    return scriptHistory;
  }

  private String getScriptVersionPath(Script script) {
    return VERSIONS_ROOT + "/" + normalizedPath(script);
  }

  private Node createVersionNode(Node parent, Script script, Session session) throws RepositoryException {
    String path = parent.getPath() + "/" + script.getChecksum();
    return JcrUtils.getOrCreateByPath(path, "sling:OrderedFolder", "sling:OrderedFolder", session, true);
  }

  private Node copyScriptContent(Node parent, Script script, Session session) throws RepositoryException {
    if (!parent.hasNode(SCRIPT_NODE_NAME)) {
      Node source = session.getNode(script.getPath());
      Node file = JcrUtil.copy(source, parent, SCRIPT_NODE_NAME);
      file.addMixin(ScriptNode.APM_SCRIPT);
      return file;
    }
    return parent.getNode(SCRIPT_NODE_NAME);
  }

  private String normalizedPath(Script script) {
    return script.getPath().replaceAll("/", "_").substring(1);
  }
}
