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

import static com.google.common.base.Preconditions.checkArgument;

import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import com.cognifide.cq.cqsm.api.exceptions.ExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Message;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.logger.Status;
import com.cognifide.cq.cqsm.api.scripts.Event;
import com.cognifide.cq.cqsm.api.scripts.EventManager;
import com.cognifide.cq.cqsm.api.scripts.ModifiableScript;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.actions.executor.ActionExecutor;
import com.cognifide.cq.cqsm.core.antlr.ScriptContext;
import com.cognifide.cq.cqsm.core.antlr.ScriptRunner;
import com.cognifide.cq.cqsm.core.loader.ScriptTree;
import com.cognifide.cq.cqsm.core.loader.ScriptTreeLoader;
import com.cognifide.cq.cqsm.core.macro.MacroRegister;
import com.cognifide.cq.cqsm.core.macro.MacroRegistrar;
import com.cognifide.cq.cqsm.core.progress.ProgressImpl;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingMode;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingPolicy;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
  private ScriptStorage scriptStorage;
  @Reference
  private ActionFactory actionFactory;
  @Reference
  private ScriptFinder scriptFinder;

  private EventManager eventManager = new EventManager();

  @Override
  public Progress evaluate(final String scriptContent, final Mode mode, final ResourceResolver resolver)
      throws RepositoryException, PersistenceException {

    Script script = scriptFinder.find(ScriptManager.FILE_FOR_EVALUATION, false, resolver);
    if (script != null) {
      scriptStorage.remove(script, resolver);
    }

    InputStream stream = new ByteArrayInputStream(scriptContent.getBytes(StandardCharsets.UTF_8));
    script = scriptStorage.save(FILE_FOR_EVALUATION, stream, true, resolver);

    Progress progress = process(script, mode, resolver);
    scriptStorage.remove(script, resolver);

    return progress;
  }

  @Override
  public synchronized Progress process(final Script script, final Mode mode, final ResourceResolver resolver)
      throws RepositoryException, PersistenceException {

    checkArgument(script != null, "Script is required");
    checkArgument(mode != null, "Mode is required");

    Progress progress;
    try {
      progress = execute(script, mode, resolver);

    } catch (ExecutionException e) {
      progress = new ProgressImpl(resolver.getUserID());
      progress.addEntry(Message.getErrorMessage(e.getMessage()), Status.ERROR);
    }
    process(script, mode, progress.isSuccess(), resolver);
    return progress;
  }

  private Progress execute(final Script script, final Mode mode, final ResourceResolver resolver)
      throws RepositoryException {

    final String path = script.getPath();

    LOG.info("Script execution started: {} [{}]", path, mode);
    final ActionExecutor actionExecutor = createExecutor(mode, resolver);
    final Context context = actionExecutor.getContext();
    final SessionSavingPolicy savingPolicy = context.getSavingPolicy();

    final ScriptTree scriptTree = new ScriptTreeLoader(resolver, scriptFinder).loadScriptTree(script);
    final MacroRegister macroRegister = new MacroRegistrar().buildMacroRegister(scriptTree);
    final ScriptContext scriptContext = new ScriptContext(resolver.getUserID(), macroRegister, scriptTree);
    eventManager.trigger(Event.BEFORE_EXECUTE, script, mode, scriptContext.getProgress());
    ScriptRunner scriptRunner = new ScriptRunner((progress, commandName, parameters) -> {
      try {
        ActionDescriptor descriptor = actionFactory.evaluate(commandName, parameters);
        ActionResult result = actionExecutor.execute(descriptor);
        progress.addEntry(descriptor, result);

        if ((Status.ERROR == result.getStatus()) && (Mode.DRY_RUN != mode)) {
          eventManager.trigger(Event.AFTER_EXECUTE, script, mode, progress);
        } else {
          savingPolicy.save(context.getSession(), SessionSavingMode.EVERY_ACTION);
        }
      } catch (RepositoryException | ActionCreationException e) {
        LOG.error("Error while processing command: {}", commandName, e);
        progress.addEntry(commandName, Message.getErrorMessage(e.getMessage()), Status.ERROR);
      }
    });

    final Progress progress = scriptRunner.execute(scriptContext);
    if (progress.isSuccess()) {
      savingPolicy.save(context.getSession(), SessionSavingMode.SINGLE);
    }

    eventManager.trigger(Event.AFTER_EXECUTE, script, mode, progress);
    return progress;
  }

  private void process(final Script script, final Mode mode, final boolean success, final ResourceResolver resolver)
      throws PersistenceException {

    final ModifiableScript modifiableScript = new ModifiableScriptWrapper(resolver, script);
    if (Arrays.asList(Mode.RUN, Mode.AUTOMATIC_RUN).contains(mode)) {
      modifiableScript.setExecuted(true);
    }

    if (Arrays.asList(Mode.DRY_RUN, Mode.RUN, Mode.AUTOMATIC_RUN).contains(mode)) {
      modifiableScript.setDryRunStatus(success);
    }

    if (mode.equals(Mode.VALIDATION)) {
      modifiableScript.setValid(success);
    }
  }

  @Override
  public EventManager getEventManager() {
    return eventManager;
  }

  @Override
  public List<Script> findIncludes(final Script script, final ResourceResolver resolver) throws ExecutionException {
    final List<Script> includes = new ArrayList<>();

    ScriptTreeLoader scriptTreeLoader = new ScriptTreeLoader(resolver, scriptFinder);
    ScriptTree scriptTree = scriptTreeLoader.loadScriptTree(script);

    return includes;
  }

  private ActionExecutor createExecutor(final Mode mode, final ResourceResolver resolver) throws RepositoryException {
    final Context context = new Context((JackrabbitSession) resolver.adaptTo(Session.class));
    return mode.getExecutor(context);
  }
}
