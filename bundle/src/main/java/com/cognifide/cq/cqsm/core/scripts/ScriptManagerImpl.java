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

import com.cognifide.apm.grammar.ReferenceFinder;
import com.cognifide.apm.grammar.ScriptRunner;
import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import com.cognifide.cq.cqsm.api.exceptions.ExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.logger.Status;
import com.cognifide.cq.cqsm.api.scripts.Event;
import com.cognifide.cq.cqsm.api.scripts.EventManager;
import com.cognifide.cq.cqsm.api.scripts.ExecutionMetadata;
import com.cognifide.cq.cqsm.api.scripts.ModifiableScript;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.actions.executor.ActionExecutor;
import com.cognifide.cq.cqsm.core.progress.ProgressImpl;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingMode;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingPolicy;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    service = ScriptManager.class,
    property = {
        Property.DESCRIPTION + "CQSM Script Manager Service",
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

  private EventManager eventManager = new EventManager();

  private Map<String, String> predefinedDefinitions;

  private Progress execute(Script script, final Mode mode, Map<String, String> customDefinitions,
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
    ScriptRunner scriptRunner = new ScriptRunner(scriptFinder, resolver, mode == Mode.VALIDATION,
        (executionContext, commandName, arguments) -> {
          try {
            context.setCurrentAuthorizable(executionContext.getAuthorizable());
            ActionDescriptor descriptor = actionFactory.evaluate(commandName, arguments);
            ActionResult result = actionExecutor.execute(descriptor);
            executionContext.setAuthorizable(context.getCurrentAuthorizableIfExists());
            progress.addEntry(descriptor, result);

            if ((Status.ERROR == result.getStatus()) && (Mode.DRY_RUN != mode)) {
              eventManager.trigger(Event.AFTER_EXECUTE, script, mode, progress);
            } else {
              savingPolicy.save(context.getSession(), SessionSavingMode.EVERY_ACTION);
            }
          } catch (RepositoryException | ActionCreationException e) {
            LOG.error("Error while processing command: {}", commandName, e);
            progress.addEntry(Status.ERROR, e.getMessage(), commandName);
          }
        });

    try {
      scriptRunner.execute(script, progress, customDefinitions);
    } catch (RuntimeException e) {
      progress.addEntry(Status.ERROR, e.getMessage());
    }
    if (progress.isSuccess()) {
      savingPolicy.save(context.getSession(), SessionSavingMode.SINGLE);
    }

    eventManager.trigger(Event.AFTER_EXECUTE, script, mode, progress);
    return progress;
  }

  @Override
  public synchronized Progress process(final Script script, final Mode mode, ResourceResolver resolver)
      throws RepositoryException, PersistenceException {
    return process(script, mode, Maps.newHashMap(), resolver);
  }

  @Override
  public Progress process(Script script, final Mode mode, final Map<String, String> customDefinitions,
      ResourceResolver resolver) throws RepositoryException, PersistenceException {
    Progress progress;
    try {
      progress = execute(script, mode, customDefinitions, resolver);

    } catch (ExecutionException e) {
      progress = new ProgressImpl(resolver.getUserID());
      progress.addEntry(Status.ERROR, e.getMessage());
    }
    process(script, mode, progress.isSuccess(), resolver);
    return progress;
  }

  private void process(final Script script, final Mode mode, final boolean success,
      ResourceResolver resolver) throws PersistenceException {
    final ModifiableScript modifiableScript = new ModifiableScriptWrapper(resolver, script);

    if (Arrays.asList(Mode.RUN, Mode.AUTOMATIC_RUN).contains(mode)) {
      modifiableScript.setExecuted(true);
    }

    if (Mode.VALIDATION.equals(mode)) {
      modifiableScript.setValid(success);
    }
  }

  @Override
  public Progress evaluate(String path, String content, Mode mode, ResourceResolver resolver)
      throws RepositoryException, PersistenceException {
    return evaluate(path, content, mode, Maps.newHashMap(), resolver);
  }

  @Override
  public Progress evaluate(String path, String content, Mode mode, Map<String, String> customDefinitions,
      ResourceResolver resolver) throws RepositoryException, PersistenceException {
    Script script = scriptFinder.find(path + "/" + ScriptManager.FILE_FOR_EVALUATION, false, resolver);
    if (script != null) {
      scriptStorage.remove(script, resolver);
    }

    InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    script = scriptStorage.save(path + "/" + FILE_FOR_EVALUATION, stream, ExecutionMetadata.onDemand(), true, resolver);

    Progress progress = process(script, mode, customDefinitions, resolver);
    scriptStorage.remove(script, resolver);

    return progress;
  }

  @Override
  public Map<String, String> getPredefinedDefinitions() {
    if (predefinedDefinitions == null) {
      predefinedDefinitions = Collections.synchronizedMap(new TreeMap<>());
      eventManager.trigger(Event.INIT_DEFINITIONS);
    }
    return predefinedDefinitions;
  }

  @Override
  public EventManager getEventManager() {
    return eventManager;
  }

  @Override
  public List<Script> findIncludes(Script script, ResourceResolver resolver) throws ExecutionException {
    ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resolver);
    return referenceFinder.findReferences(script);
  }

  private ActionExecutor createExecutor(Mode mode, ResourceResolver resolver) throws RepositoryException {
    final Context context = new Context((JackrabbitSession) resolver.adaptTo(Session.class));
    return mode.getExecutor(context, actionFactory);
  }
}
