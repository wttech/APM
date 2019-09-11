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
package com.cognifide.cq.cqsm.core.history;

import static com.cognifide.cq.cqsm.core.utils.sling.SlingHelper.resolveDefault;
import static com.day.crx.JcrConstants.NT_UNSTRUCTURED;

import com.cognifide.actions.api.ActionSendException;
import com.cognifide.actions.api.ActionSubmitter;
import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.history.History;
import com.cognifide.cq.cqsm.api.history.HistoryEntry;
import com.cognifide.cq.cqsm.api.history.InstanceDetails;
import com.cognifide.cq.cqsm.api.history.InstanceDetails.InstanceType;
import com.cognifide.cq.cqsm.api.history.ScriptHistory;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.progress.ProgressHelper;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.history.HistoryEntryWriter.HistoryEntryWriterBuilder;
import com.cognifide.cq.cqsm.core.scripts.ScriptContent;
import com.cognifide.cq.cqsm.core.utils.sling.ResolveCallback;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationAction;
import com.google.common.collect.Lists;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    service = History.class,
    property = {
        Property.DESCRIPTION + "CQSM History Service",
        Property.VENDOR
    }
)
public class HistoryImpl implements History {

  private static final Logger LOG = LoggerFactory.getLogger(HistoryImpl.class);

  private static final String HISTORY_FOLDER = "/conf/apm/history";

  private static final String HISTORY_ENTRIES_QUERY = String.format("SELECT * FROM [nt:unstructured] "
      + " WHERE ISDESCENDANTNODE([%s]) AND filePath IS NOT NULL "
      + " ORDER BY executionTime DESC ", HISTORY_FOLDER);

  public static final String REPLICATE_ACTION = "com/cognifide/actions/cqsm/history/replicate";

  @Reference
  private ActionSubmitter actionSubmitter;

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Reference
  private InstanceTypeProvider instanceTypeProvider;

  @Override
  public HistoryEntry logLocal(Script script, Mode mode, Progress progressLogger) {
    InstanceType instanceDetails = instanceTypeProvider.isOnAuthor() ? InstanceType.AUTHOR : InstanceType.PUBLISH;
    return resolveDefault(resolverFactory, progressLogger.getExecutor(), (ResolveCallback<HistoryEntry>) resolver -> {
      final HistoryEntryWriter historyEntryWriter = createBuilder(resolver, script, mode, progressLogger)
          .executionTime(Calendar.getInstance())
          .instanceType(instanceDetails.getInstanceName())
          .instanceHostname(getHostname())
          .build();
      return createHistoryEntry(resolver, script, mode, historyEntryWriter, false);
    }, null);
  }

  @Override
  public HistoryEntry logRemote(Script script, Mode mode, Progress progressLogger, InstanceDetails instanceDetails,
      Calendar executionTime) {
    return resolveDefault(resolverFactory, progressLogger.getExecutor(), (ResolveCallback<HistoryEntry>) resolver -> {
      final HistoryEntryWriter historyEntryWriter = createBuilder(resolver, script, mode, progressLogger)
          .executionTime(executionTime)
          .instanceType(instanceDetails.getInstanceType().getInstanceName())
          .instanceHostname(instanceDetails.getHostname())
          .build();
      return createHistoryEntry(resolver, script, mode, historyEntryWriter, true);
    }, null);
  }

  private HistoryEntryWriterBuilder createBuilder(ResourceResolver resolver, Script script, Mode mode,
      Progress progressLogger) {
    Resource source = resolver.getResource(script.getPath());
    return HistoryEntryWriter.builder()
        .author(source.getValueMap().get(JcrConstants.JCR_CREATED_BY, StringUtils.EMPTY))
        .executor(resolver.getUserID())
        .fileName(source.getName())
        .filePath(source.getPath())
        .isRunSuccessful(progressLogger.isSuccess())
        .mode(mode.toString())
        .progressLog(ProgressHelper.toJson(progressLogger.getEntries()));
  }

  @Override
  public List<Resource> findAllResources(ResourceResolver resourceResolver) {
    Iterator<Resource> resources = resourceResolver.findResources(HISTORY_ENTRIES_QUERY, Query.JCR_SQL2);
    return Lists.newArrayList(resources);
  }

  @Override
  public List<HistoryEntry> findAllHistoryEntries(ResourceResolver resourceResolver) {
    return findAllResources(resourceResolver)
        .stream()
        .map(resource -> resource.adaptTo(HistoryEntryImpl.class))
        .collect(Collectors.toList());
  }

  @Override
  public void replicate(final HistoryEntry entry, String userId) {
    if (actionSubmitter == null) {
      LOG.warn(String.format("History entry '%s' replication cannot be performed on author instance",
          entry.getPath()));
      return;
    }
    SlingHelper.operateTraced(resolverFactory, userId, resolver -> {
      Resource resource = resolver.getResource(entry.getPath());
      if (resource != null) {
        try {
          LOG.warn("Sending action {} to action submitter", REPLICATE_ACTION);
          Map<String, Object> properties = new HashMap<>(resource.getValueMap());
          properties.put(ReplicationAction.PROPERTY_USER_ID, resolver.getUserID());
          actionSubmitter.sendAction(REPLICATE_ACTION, properties);
          LOG.warn("Action {} was sent to action submitter", REPLICATE_ACTION);
        } catch (ActionSendException e) {
          LOG.info("Cannot send action", e);
        }
      }
    });
  }

  @Override
  public HistoryEntry findHistoryEntry(ResourceResolver resourceResolver, final String path) {
    Resource resource = resourceResolver.getResource(path);
    if (resource != null) {
      return resource.adaptTo(HistoryEntryImpl.class);
    }
    return null;
  }

  @Override
  public ScriptHistory findScriptHistory(ResourceResolver resourceResolver, Script script) {
    Resource resource = resourceResolver.getResource(getScriptHistoryPath(script));
    if (resource != null) {
      ScriptHistoryImpl scriptHistory = resource.adaptTo(ScriptHistoryImpl.class);
      scriptHistory.updateScript(script);
      return scriptHistory;
    }
    return ScriptHistoryImpl.empty(script.getPath());
  }

  private HistoryEntry createHistoryEntry(ResourceResolver resolver, Script script, Mode mode,
      HistoryEntryWriter historyEntryWriter, boolean remote) {
    try {
      Resource source = resolver.getResource(script.getPath());
      Node historyEntryNode = createHistoryEntryNode(resolver, script, mode, remote);
      writeProperties(resolver, historyEntryNode, historyEntryWriter);
      copyScriptContent(source, historyEntryNode);
      resolver.commit();
      return resolver.getResource(historyEntryNode.getPath()).adaptTo(HistoryEntry.class);
    } catch (PersistenceException | RepositoryException e) {
      LOG.error("Issues with saving to repository while logging script execution", e);
      return null;
    }
  }

  @NotNull
  private Resource writeProperties(ResourceResolver resolver, Node historyEntry, HistoryEntryWriter historyEntryWriter)
      throws RepositoryException {
    Resource entryResource = resolver.getResource(historyEntry.getPath());
    historyEntryWriter.writeTo(entryResource);
    return entryResource;
  }

  private Node createHistoryEntryNode(ResourceResolver resolver, Script script, Mode mode, boolean remote)
      throws RepositoryException {
    Session session = resolver.adaptTo(Session.class);
    String path = getScriptHistoryPath(script);
    Node scriptHistory = JcrUtils
        .getOrCreateByPath(path, "sling:OrderedFolder", NT_UNSTRUCTURED, session, true);
    scriptHistory.setProperty("scriptPath", script.getPath());
    String modeName = (remote ? "Remote" : "Local");
    modeName += WordUtils.capitalizeFully(mode.toString().toLowerCase(), new char[]{'_'}).replace("_", "");
    Node historyEntry = JcrUtils.getOrCreateUniqueByPath(scriptHistory, modeName, NT_UNSTRUCTURED);
    historyEntry.setProperty("checksum", script.getChecksumValue());
    scriptHistory.setProperty("last" + modeName, historyEntry.getPath());
    session.save();
    return historyEntry;
  }

  @NotNull
  private String getScriptHistoryPath(Script script) {
    return HISTORY_FOLDER + "/" + script.getPath().replace("/", "_");
  }

  private void copyScriptContent(Resource source, Node target) throws RepositoryException {
    Node file = JcrUtil.copy(source.adaptTo(Node.class), target, "script");
    file.addMixin(ScriptContent.CQSM_FILE);
  }

  private String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      return null;
    }
  }
}