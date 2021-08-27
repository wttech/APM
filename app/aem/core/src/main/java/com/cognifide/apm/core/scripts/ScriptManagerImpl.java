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
package com.cognifide.apm.core.scripts;

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.actions.SessionSavingMode;
import com.cognifide.apm.api.actions.SessionSavingPolicy;
import com.cognifide.apm.api.exceptions.ActionCreationException;
import com.cognifide.apm.api.exceptions.ExecutionException;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.DefinitionsProvider;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.actions.ActionDescriptor;
import com.cognifide.apm.core.actions.ActionFactory;
import com.cognifide.apm.core.actions.executor.ActionExecutor;
import com.cognifide.apm.core.actions.executor.ActionExecutorFactory;
import com.cognifide.apm.core.executors.ContextImpl;
import com.cognifide.apm.core.grammar.ScriptRunner;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.history.HistoryEntry;
import com.cognifide.apm.core.logger.Progress;
import com.cognifide.apm.core.progress.ProgressImpl;
import com.cognifide.apm.core.services.event.ApmEvent.ScriptExecutedEvent;
import com.cognifide.apm.core.services.event.ApmEvent.ScriptLaunchedEvent;
import com.cognifide.apm.core.services.event.EventManager;
import com.cognifide.apm.core.services.version.VersionService;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    service = {ScriptManager.class},
    property = {
        Property.DESCRIPTION + "APM Script Manager Service",
        Property.VENDOR
    }
)
public class ScriptManagerImpl implements ScriptManager {

  private static final Logger LOG = LoggerFactory.getLogger(ScriptManagerImpl.class);

  @Reference
  private ActionFactory actionFactory;

  @Reference
  private ScriptStorage scriptStorage;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private SlingSettingsService slingSettings;

  @Reference
  private VersionService versionService;

  @Reference
  private EventManager eventManager;

  @Reference
  private History history;

  @Reference(
      cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC,
      service = DefinitionsProvider.class
  )
  private final Set<DefinitionsProvider> definitionsProviders = new CopyOnWriteArraySet<>();

  private Progress execute(Script script, final ExecutionMode mode, Map<String, String> customDefinitions,
                           ResourceResolver resolver) throws ExecutionException, RepositoryException {
    if (script == null) {
      throw new ExecutionException("Script is not specified");
    }

    if (mode == null) {
      throw new ExecutionException("Execution mode is not specified");
    }

    final String path = script.getPath();

    LOG.info(String.format("Script execution started: %s [%s]", path, mode));
    final Progress progress = new ProgressImpl(resolver.getUserID());
    final ActionExecutor actionExecutor = createExecutor(mode, resolver);
    final Context context = actionExecutor.getContext();
    final SessionSavingPolicy savingPolicy = context.getSavingPolicy();

    eventManager.trigger(new ScriptLaunchedEvent(script, mode));
    ScriptRunner scriptRunner = new ScriptRunner(scriptFinder, resolver, mode == ExecutionMode.VALIDATION,
        (executionContext, commandName, arguments) -> {
          try {
            context.setCurrentAuthorizable(executionContext.getAuthorizable());
            ActionDescriptor descriptor = actionFactory.evaluate(commandName, arguments);
            ActionResult result = actionExecutor.execute(descriptor);
            executionContext.setAuthorizable(context.getCurrentAuthorizableIfExists());
            progress.addEntry(descriptor, result);

            if ((Status.ERROR != result.getStatus()) || (ExecutionMode.DRY_RUN == mode)) {
              savingPolicy.save(context.getSession(), SessionSavingMode.EVERY_ACTION);
            }
            return result.getStatus();
          } catch (RepositoryException | ActionCreationException e) {
            LOG.error("Error while processing command: {}", commandName, e);
            progress.addEntry(Status.ERROR, e.getMessage(), commandName);
            return Status.ERROR;
          }
        });

    try {
      Map<String, String> definitions = new HashMap<>();
      definitions.putAll(getPredefinedDefinitions());
      definitions.putAll(customDefinitions);
      scriptRunner.execute(script, progress, definitions);
    } catch (RuntimeException e) {
      progress.addEntry(Status.ERROR, e.getMessage());
    }
    if (progress.isSuccess()) {
      savingPolicy.save(context.getSession(), SessionSavingMode.SINGLE);
    }
    return progress;
  }

  @Override
  public Progress process(final Script script, final ExecutionMode mode, ResourceResolver resolver)
      throws RepositoryException, PersistenceException {
    return process(script, mode, Maps.newHashMap(), resolver);
  }

  @Override
  public Progress process(Script script, final ExecutionMode mode, final Map<String, String> customDefinitions,
                          ResourceResolver resolver) throws RepositoryException, PersistenceException {
    Progress progress;
    try {
      progress = execute(script, mode, customDefinitions, resolver);
    } catch (ExecutionException e) {
      progress = new ProgressImpl(resolver.getUserID());
      progress.addEntry(Status.ERROR, e.getMessage());
    }

    updateScriptProperties(script, mode, progress.isSuccess());
    versionService.updateVersionIfNeeded(resolver, script);
    saveHistory(script, mode, progress);
    eventManager.trigger(new ScriptExecutedEvent(script, mode, progress.isSuccess()));

    return progress;
  }

  private void saveHistory(Script script, ExecutionMode mode, Progress progress) {
    if (slingSettings.getRunModes().contains("author")) {
      if (mode != ExecutionMode.VALIDATION) {
        history.logLocal(script, mode, progress);
      }
    } else {
      if (mode.isRun()) {
        try {
          HistoryEntry entry = history.logLocal(script, mode, progress);
          history.replicate(entry, progress.getExecutor());
        } catch (RepositoryException e) {
          LOG.error("Repository error occurred while replicating script execution", e);
        }
      }
    }
  }

  private void updateScriptProperties(final Script script, final ExecutionMode mode, final boolean success)
      throws PersistenceException {

    final MutableScriptWrapper mutableScriptWrapper = new MutableScriptWrapper(script);

    if (Arrays.asList(ExecutionMode.RUN, ExecutionMode.AUTOMATIC_RUN).contains(mode)) {
      mutableScriptWrapper.setExecuted(true);
    }

    if (ExecutionMode.VALIDATION.equals(mode)) {
      mutableScriptWrapper.setValid(success);
    }
  }

  @Override
  public Map<String, String> getPredefinedDefinitions() {
    Map<String, String> predefinedDefinitions = new HashMap<>();
    definitionsProviders.forEach(provider -> predefinedDefinitions.putAll(provider.getPredefinedDefinitions()));
    return predefinedDefinitions;
  }

  private ActionExecutor createExecutor(ExecutionMode mode, ResourceResolver resolver) throws RepositoryException {
    final Context context = new ContextImpl((JackrabbitSession) resolver.adaptTo(Session.class));
    return ActionExecutorFactory.create(mode, context, actionFactory);
  }
}
