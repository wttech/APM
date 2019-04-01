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

import com.cognifide.actions.api.ActionSendException;
import com.cognifide.actions.api.ActionSubmitter;
import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.history.Entry;
import com.cognifide.cq.cqsm.api.history.InstanceDetails;
import com.cognifide.cq.cqsm.api.history.InstanceDetails.InstanceType;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.progress.ProgressHelper;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.history.HistoryLogWriter.HistoryLogWriterBuilder;
import com.cognifide.cq.cqsm.core.scripts.ModifiableScriptWrapper;
import com.cognifide.cq.cqsm.core.scripts.ScriptContent;
import com.cognifide.cq.cqsm.core.utils.sling.ResolveCallback;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationAction;
import com.google.common.collect.ImmutableList;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
@Service
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM History Service"),
    @Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)
})
public class HistoryImpl implements History {

  private static final Logger LOG = LoggerFactory.getLogger(HistoryImpl.class);

  private static final String HISTORY_PATH = "/conf/apm/history";

  public static final String REPLICATE_ACTION = "com/cognifide/actions/cqsm/history/replicate";

  @Reference
  private ActionSubmitter actionSubmitter;

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Reference
  private InstanceTypeProvider instanceTypeProvider;

  @Override
  public Entry log(Script script, Mode mode, Progress progressLogger) {
    InstanceType instanceDetails = instanceTypeProvider.isOnAuthor() ? InstanceType.AUTHOR : InstanceType.PUBLISH;
    return resolveDefault(resolverFactory, progressLogger.getExecutor(), (ResolveCallback<Entry>) resolver -> {
      final HistoryLogWriter historyLogWriter = createHistoryLogWriterBuilder(resolver, script, mode, progressLogger)
          .executionTime(Calendar.getInstance())
          .instanceType(instanceDetails.getInstanceName())
          .instanceHostname(getHostname())
          .build();
      return log(resolver, script, mode, historyLogWriter);
    }, null);
  }

  @Override
  public Entry logRemote(Script script, Mode mode, Progress progressLogger, InstanceDetails instanceDetails,
      Calendar executionTime) {
    return resolveDefault(resolverFactory, progressLogger.getExecutor(), (ResolveCallback<Entry>) resolver -> {
      final HistoryLogWriter historyLogWriter = createHistoryLogWriterBuilder(resolver, script, mode, progressLogger)
          .executionTime(executionTime)
          .instanceType(instanceDetails.getInstanceType().getInstanceName())
          .instanceHostname(instanceDetails.getHostname())
          .build();
      return log(resolver, script, mode, historyLogWriter);
    }, null);
  }

  private HistoryLogWriterBuilder createHistoryLogWriterBuilder(ResourceResolver resolver, Script script, Mode mode,
      Progress progressLogger) {
    Resource source = resolver.getResource(script.getPath());
    return HistoryLogWriter.builder()
        .author(source.getValueMap().get(JcrConstants.JCR_CREATED_BY, StringUtils.EMPTY))
        .executor(getExecutor(resolver, mode))
        .fileName(source.getName())
        .filePath(source.getPath())
        .isRunSuccessful(progressLogger.isSuccess())
        .mode(mode.toString())
        .progressLog(ProgressHelper.toJson(progressLogger.getEntries()));
  }

  @Override
  public List<Entry> findAll() {
    return resolveDefault(resolverFactory, (ResolveCallback<List<Entry>>) resolver -> findAllResource(resolver)
        .stream()
        .map(resource -> resource.adaptTo(Entry.class))
        .collect(Collectors.toList()), Collections.<Entry>emptyList());
  }

  @Override
  public List<Resource> findAllResource(ResourceResolver resourceResolver) {
    final Resource historyFolder = resourceResolver.getResource(HISTORY_PATH);
    return Optional.ofNullable(historyFolder)
        .map(resource -> resource.getChildren())
        .map(elements -> (List<Resource>) ImmutableList.copyOf(elements))
        .orElseGet(() -> {
          LOG.warn("History resource can't be found at: {}", HISTORY_PATH);
          return Collections.emptyList();
        });
  }

  @Override
  public void replicate(final Entry entry, String userId) {
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
  public Entry find(final String path) {
    return resolveDefault(resolverFactory, (ResolveCallback<Entry>) resolver -> {
      Entry result = null;
      for (String resourcePath : Arrays.asList(path, path.replace("_cqsm", ".cqsm"))) {
        Resource resource = resolver.getResource(resourcePath);
        if (resource != null) {
          result = resource.adaptTo(Entry.class);
          break;
        }
      }
      return result;
    }, null);
  }

  private Entry log(ResourceResolver resolver, Script script, Mode mode, HistoryLogWriter historyLogWriter) {
    Entry result = null;
    try {
      HistoryLogStrategy historyLogStrategy = createHistoryLogStrategy(mode);
      Resource source = resolver.getResource(script.getPath());
      Resource historyLogResource = historyLogStrategy.getHistoryLogResource(resolver, source.getName());
      historyLogWriter.writeTo(historyLogResource);

      copyScriptContent(source, historyLogResource);
      updateScriptDetails(resolver, script, mode, historyLogResource);
      resolver.commit();
      result = resolver.getResource(historyLogResource.getPath()).adaptTo(Entry.class);
    } catch (RepositoryException | PersistenceException e) {
      LOG.error("Issues with saving to repository while logging script execution", e);
    }
    return result;
  }

  private void updateScriptDetails(ResourceResolver resolver, Script script, Mode mode, Resource historyLogResource)
      throws PersistenceException {
    ModifiableScriptWrapper scriptWrapper = new ModifiableScriptWrapper(resolver, script);
    if (mode == Mode.DRY_RUN) {
      scriptWrapper.setDryRunSummary(historyLogResource.getPath());
    } else {
      scriptWrapper.setExecutionSummary(historyLogResource.getPath());
    }
  }

  private void copyScriptContent(Resource source, Resource target) throws RepositoryException {
    Node file = JcrUtil.copy(source.adaptTo(Node.class), target.adaptTo(Node.class), "script");
    file.addMixin(ScriptContent.CQSM_FILE);
  }

  private HistoryLogStrategy createHistoryLogStrategy(Mode mode) {
    return mode == Mode.DRY_RUN ? new DryRunHistoryLogStrategy(HISTORY_PATH)
        : new DefaultHistoryLogStrategy(HISTORY_PATH);
  }

  private String getExecutor(ResourceResolver resourceResolver, Mode mode) {
    String executor = null;
    if (mode.isRun()) {
      executor = resourceResolver.getUserID();
    }
    return executor;
  }

  private String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      return null;
    }
  }
}