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

import static com.cognifide.apm.core.services.ChecksumProviderKt.applyChecksum;

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
import com.cognifide.apm.core.grammar.ScriptRunner;
import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.cq.cqsm.api.executors.ContextImpl;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.Event;
import com.cognifide.cq.cqsm.api.scripts.EventManager;
import com.cognifide.cq.cqsm.api.scripts.ExtendedScriptManager;
import com.cognifide.cq.cqsm.api.scripts.LaunchMetadata;
import com.cognifide.cq.cqsm.api.scripts.ModifiableScript;
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.actions.executor.ActionExecutor;
import com.cognifide.cq.cqsm.core.actions.executor.ActionExecutorFactory;
import com.cognifide.cq.cqsm.core.progress.ProgressImpl;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    service = {ScriptManager.class, ExtendedScriptManager.class},
    property = {
        Property.DESCRIPTION + "CQSM Script Manager Service",
        Property.VENDOR
    }
)
public class ScriptManagerImpl implements ExtendedScriptManager {

  private static final Logger LOG = LoggerFactory.getLogger(ScriptManagerImpl.class);

  @Reference
  private ActionFactory actionFactory;

  @Reference
  private ScriptStorage scriptStorage;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference(
      cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC,
      service = DefinitionsProvider.class
  )
  private final Set<DefinitionsProvider> definitionsProviders = new CopyOnWriteArraySet<>();

  private final EventManager eventManager = new EventManager();

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

    eventManager.trigger(Event.BEFORE_EXECUTE, script, mode, progress);
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
          } catch (RepositoryException | ActionCreationException e) {
            LOG.error("Error while processing command: {}", commandName, e);
            progress.addEntry(Status.ERROR, e.getMessage(), commandName);
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
  public synchronized Progress process(final Script script, final ExecutionMode mode, ResourceResolver resolver)
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

    updateScriptProperties(script, mode, progress.isSuccess(), resolver);
    applyChecksum(scriptFinder, resolver, script);
    eventManager.trigger(Event.AFTER_EXECUTE, script, mode, progress);

    return progress;
  }

  private void updateScriptProperties(final Script script, final ExecutionMode mode, final boolean success,
      ResourceResolver resolver) throws PersistenceException {
    final ModifiableScript modifiableScript = new ModifiableScriptWrapper(resolver, script);

    if (Arrays.asList(ExecutionMode.RUN, ExecutionMode.AUTOMATIC_RUN).contains(mode)) {
      modifiableScript.setExecuted(true);
    }

    if (ExecutionMode.VALIDATION.equals(mode)) {
      modifiableScript.setValid(success);
    }
  }

  @Override
  public Progress evaluate(String path, String content, ExecutionMode mode, ResourceResolver resolver)
      throws RepositoryException, PersistenceException {
    return evaluate(path, content, mode, Maps.newHashMap(), resolver);
  }

  @Override
  public Progress evaluate(String path, String content, ExecutionMode mode, Map<String, String> customDefinitions,
      ResourceResolver resolver) throws RepositoryException, PersistenceException {
    Script script = scriptFinder.find(path, false, resolver);
    if (script != null) {
      path = StringUtils.substringBeforeLast(path, "/");
    }

    InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    script = scriptStorage.save(path + "/" + FILE_FOR_EVALUATION, stream, LaunchMetadata.onDemand(), true, resolver);

    Progress progress = process(script, mode, customDefinitions, resolver);
    scriptStorage.remove(script, resolver);

    return progress;
  }

  @Override
  public Map<String, String> getPredefinedDefinitions() {
    Map<String, String> predefinedDefinitions = new HashMap<>();
    definitionsProviders.forEach(provider -> predefinedDefinitions.putAll(provider.getPredefinedDefinitions()));
    return predefinedDefinitions;
  }

  @Override
  public EventManager getEventManager() {
    return eventManager;
  }

  private ActionExecutor createExecutor(ExecutionMode mode, ResourceResolver resolver) throws RepositoryException {
    final Context context = new ContextImpl((JackrabbitSession) resolver.adaptTo(Session.class));
    return ActionExecutorFactory.create(mode, context, actionFactory);
  }
}
