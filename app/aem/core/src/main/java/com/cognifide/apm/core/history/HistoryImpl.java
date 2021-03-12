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
package com.cognifide.apm.core.history;

import static com.cognifide.apm.core.utils.sling.SlingHelper.resolveDefault;
import static com.day.crx.JcrConstants.NT_UNSTRUCTURED;
import static org.apache.jackrabbit.commons.JcrUtils.getOrCreateByPath;
import static org.apache.jackrabbit.commons.JcrUtils.getOrCreateUniqueByPath;

import com.adobe.aemds.guide.utils.JcrResourceConstants;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.history.HistoryEntryWriter.HistoryEntryWriterBuilder;
import com.cognifide.apm.core.history.InstanceDetails.InstanceType;
import com.cognifide.apm.core.logger.Progress;
import com.cognifide.apm.core.progress.ProgressHelper;
import com.cognifide.apm.core.services.version.VersionService;
import com.cognifide.apm.core.utils.InstanceTypeProvider;
import com.cognifide.apm.core.utils.sling.ResolveCallback;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import com.day.cq.commons.jcr.JcrConstants;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = History.class,
    property = {
        Property.DESCRIPTION + "APM History Service",
        Property.VENDOR
    }
)
@Slf4j
public class HistoryImpl implements History {

  public static final String HISTORY_FOLDER = "/var/apm/history";

  private static final String APM_HISTORY = "apmHistory";

  private static final String APM_HISTORY_SCRIPT = "script";

  private static final String APM_HISTORY_ENTRY = "entry";

  private static final String HISTORY_ENTRIES_QUERY = String.format("SELECT * FROM [nt:unstructured] "
      + " WHERE ISDESCENDANTNODE([%s]) AND apmHistory = '%s' "
      + " ORDER BY executionTime DESC ", HISTORY_FOLDER, APM_HISTORY_ENTRY);

  @Reference
  private RemoteScriptExecutionNotifier remoteScriptExecutionNotifier;

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Reference
  private InstanceTypeProvider instanceTypeProvider;

  @Reference
  private VersionService versionService;

  @Override
  public HistoryEntry logLocal(Script script, ExecutionMode mode, Progress progressLogger) {
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
  public HistoryEntry logRemote(Script script, ExecutionMode mode, Progress progressLogger,
      InstanceDetails instanceDetails,
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

  private HistoryEntryWriterBuilder createBuilder(ResourceResolver resolver, Script script, ExecutionMode mode,
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
    SlingHelper.operateTraced(resolverFactory, userId, resolver -> {
      Resource resource = resolver.getResource(entry.getPath());
      if (resource != null) {
        Map<String, Object> properties = new HashMap<>(resource.getValueMap());
        properties.put(ReplicationAction.PROPERTY_USER_ID, resolver.getUserID());
        remoteScriptExecutionNotifier.notifyRemoteInstance(properties);
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
      return resource.adaptTo(ScriptHistoryImpl.class);
    }
    return ScriptHistoryImpl.empty(script.getPath());
  }

  private HistoryEntry createHistoryEntry(ResourceResolver resolver, Script script, ExecutionMode mode,
      HistoryEntryWriter historyEntryWriter, boolean remote) {
    try {
      Session session = resolver.adaptTo(Session.class);

      Node scriptHistoryNode = createScriptHistoryNode(script, session);
      Node historyEntryNode = createHistoryEntryNode(scriptHistoryNode, script, mode, remote);
      historyEntryNode.setProperty(HistoryEntryImpl.SCRIPT_CONTENT_PATH, versionService.getVersionPath(script));
      writeProperties(resolver, historyEntryNode, historyEntryWriter);

      session.save();
      resolver.commit();
      return resolver.getResource(historyEntryNode.getPath()).adaptTo(HistoryEntryImpl.class);
    } catch (PersistenceException | RepositoryException e) {
      log.error("Issues with saving to repository while logging script execution", e);
      return null;
    }
  }

  @NotNull
  private Resource writeProperties(ResourceResolver resolver, Node historyEntry, HistoryEntryWriter
      historyEntryWriter)
      throws RepositoryException {
    Resource entryResource = resolver.getResource(historyEntry.getPath());
    historyEntryWriter.writeTo(entryResource);
    return entryResource;
  }

  private Node createHistoryEntryNode(Node scriptHistoryNode, Script script, ExecutionMode mode, boolean remote)
      throws RepositoryException {
    String modeName = getModeName(mode, remote);
    Node historyEntry = getOrCreateUniqueByPath(scriptHistoryNode, modeName, NT_UNSTRUCTURED);
    historyEntry.setProperty(APM_HISTORY, APM_HISTORY_ENTRY);
    historyEntry.setProperty(HistoryEntryImpl.CHECKSUM, script.getChecksum());
    scriptHistoryNode.setProperty(ScriptHistoryImpl.LAST_CHECKSUM, script.getChecksum());
    scriptHistoryNode.setProperty("last" + modeName, historyEntry.getPath());
    return historyEntry;
  }

  @NotNull
  private Node createScriptHistoryNode(Script script, Session session) throws RepositoryException {
    String path = getScriptHistoryPath(script);
    Node scriptHistory = getOrCreateByPath(path, JcrResourceConstants.NT_SLING_ORDERED_FOLDER, NT_UNSTRUCTURED, session, true);
    scriptHistory.setProperty(APM_HISTORY, APM_HISTORY_SCRIPT);
    scriptHistory.setProperty(ScriptHistoryImpl.SCRIPT_PATH, script.getPath());
    return scriptHistory;
  }

  @NotNull
  private String getModeName(ExecutionMode mode, boolean remote) {
    String modeName = (remote ? "Remote" : "Local");
    if (mode == ExecutionMode.AUTOMATIC_RUN || mode == ExecutionMode.RUN) {
      modeName += "Run";
    } else {
      modeName += "DryRun";
    }
    modeName = modeName.replace("_", "");
    return modeName;
  }

  @NotNull
  private String getScriptHistoryPath(Script script) {
    return HISTORY_FOLDER + "/" + script.getPath().replace("/", "_").substring(1);
  }

  private String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      return null;
    }
  }
}