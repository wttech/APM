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

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.history.HistoryEntryWriter.HistoryEntryWriterBuilder;
import com.cognifide.apm.core.logger.Progress;
import com.cognifide.apm.core.progress.ProgressHelper;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.services.version.VersionService;
import com.cognifide.apm.core.utils.RuntimeUtils;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import com.day.cq.commons.jcr.JcrConstants;
import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.AbstractResourceVisitor;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    property = {
        Property.DESCRIPTION + "APM History Service",
        Property.VENDOR
    }
)
public class HistoryImpl implements History {

  public static final String HISTORY_FOLDER = "/var/apm/history";

  private static final Logger LOG = LoggerFactory.getLogger(HistoryImpl.class);

  private static final String APM_HISTORY = "apmHistory";

  private static final String APM_HISTORY_SCRIPT = "script";

  private static final String APM_HISTORY_ENTRY = "entry";

  private static final String SLING_ORDERED_FOLDER = "sling:OrderedFolder";

  @Reference
  private ResourceResolverProvider resolverProvider;

  @Reference
  private VersionService versionService;

  @Override
  public HistoryEntry logLocal(Script script, ExecutionMode mode, Progress progressLogger) {
    return SlingHelper.resolveDefault(resolverProvider, resolver -> {
      HistoryEntryWriter historyEntryWriter = createBuilder(resolver, script, mode, progressLogger)
          .executionTime(Calendar.getInstance())
          .build();
      return createHistoryEntry(resolver, script, mode, historyEntryWriter);
    }, null);
  }

  private HistoryEntryWriterBuilder createBuilder(ResourceResolver resolver, Script script, ExecutionMode mode,
      Progress progressLogger) {
    Resource source = resolver.getResource(script.getPath());
    return HistoryEntryWriter.builder()
        .author(source.getValueMap().get(JcrConstants.JCR_CREATED_BY, StringUtils.EMPTY))
        .executor(progressLogger.getExecutor())
        .fileName(source.getName())
        .filePath(source.getPath())
        .isRunSuccessful(progressLogger.isSuccess())
        .mode(mode.toString())
        .progressLog(ProgressHelper.toJson(progressLogger.getEntries()))
        .instanceName(ManagementFactory.getRuntimeMXBean().getName())
        .compositeNodeStore(RuntimeUtils.determineCompositeNodeStore(resolver));
  }

  @Override
  public List<Resource> findAllResources(ResourceResolver resourceResolver) {
    List<Resource> resources = new LinkedList<>();
    AbstractResourceVisitor visitor = new AbstractResourceVisitor() {
      @Override
      protected void visit(Resource resource) {
        ValueMap valueMap = resource.getValueMap();
        if (valueMap.containsKey("executionTime") && "entry".equals(valueMap.get("apmHistory", String.class))) {
          resources.add(resource);
        }
      }
    };
    Resource rootResource = resourceResolver.getResource(HistoryImpl.HISTORY_FOLDER);
    visitor.accept(rootResource);
    return resources.stream()
        .sorted(this::compareExecutionTime)
        .collect(Collectors.toList());
  }

  private int compareExecutionTime(Resource resource1, Resource resource2) {
    Calendar executionTime1 = resource1.getValueMap().get(HistoryEntryImpl.EXECUTION_TIME, Calendar.class);
    Calendar executionTime2 = resource2.getValueMap().get(HistoryEntryImpl.EXECUTION_TIME, Calendar.class);
    return executionTime2.compareTo(executionTime1);
  }

  @Override
  public List<HistoryEntry> findAllHistoryEntries(ResourceResolver resourceResolver) {
    return findAllResources(resourceResolver)
        .stream()
        .map(resource -> resource.adaptTo(HistoryEntryImpl.class))
        .collect(Collectors.toList());
  }

  @Override
  public HistoryEntry findHistoryEntry(ResourceResolver resourceResolver, String path) {
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
      HistoryEntryWriter historyEntryWriter) {
    try {
      Session session = resolver.adaptTo(Session.class);

      Node scriptHistoryNode = createScriptHistoryNode(script, session);
      Node historyEntryNode = createHistoryEntryNode(scriptHistoryNode, script, mode);
      historyEntryNode.setProperty(HistoryEntryImpl.SCRIPT_CONTENT_PATH, versionService.getVersionPath(script));
      writeProperties(resolver, historyEntryNode, historyEntryWriter);

      session.save();
      resolver.commit();
      return resolver.getResource(historyEntryNode.getPath()).adaptTo(HistoryEntryImpl.class);
    } catch (PersistenceException | RepositoryException e) {
      LOG.error("Issues with saving to repository while logging script execution", e);
      return null;
    }
  }

  private Resource writeProperties(ResourceResolver resolver, Node historyEntry, HistoryEntryWriter
      historyEntryWriter)
      throws RepositoryException {
    Resource entryResource = resolver.getResource(historyEntry.getPath());
    historyEntryWriter.writeTo(entryResource);
    return entryResource;
  }

  private Node createHistoryEntryNode(Node scriptHistoryNode, Script script, ExecutionMode mode)
      throws RepositoryException {
    String modeName = getModeName(mode);
    Node historyEntry = JcrUtils.getOrCreateUniqueByPath(scriptHistoryNode, modeName, JcrConstants.NT_UNSTRUCTURED);
    historyEntry.setProperty(APM_HISTORY, APM_HISTORY_ENTRY);
    historyEntry.setProperty(HistoryEntryImpl.CHECKSUM, script.getChecksum());
    scriptHistoryNode.setProperty(ScriptHistoryImpl.LAST_CHECKSUM, script.getChecksum());
    scriptHistoryNode.setProperty("last" + modeName, historyEntry.getPath());
    return historyEntry;
  }

  private Node createScriptHistoryNode(Script script, Session session) throws RepositoryException {
    String path = getScriptHistoryPath(script);
    Node scriptHistory = JcrUtils.getOrCreateByPath(path, SLING_ORDERED_FOLDER, JcrConstants.NT_UNSTRUCTURED, session, true);
    scriptHistory.setProperty(APM_HISTORY, APM_HISTORY_SCRIPT);
    scriptHistory.setProperty(ScriptHistoryImpl.SCRIPT_PATH, script.getPath());
    return scriptHistory;
  }

  private String getModeName(ExecutionMode mode) {
    return (mode == ExecutionMode.AUTOMATIC_RUN || mode == ExecutionMode.RUN) ? "LocalRun" : "LocalDryRun";
  }

  private String getScriptHistoryPath(Script script) {
    return HISTORY_FOLDER + "/" + script.getPath().replace("/", "_").substring(1);
  }

}