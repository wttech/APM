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

import com.google.common.collect.Maps;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.actions.interfaces.DefinitionProvider;
import com.cognifide.cq.cqsm.api.actions.interfaces.ScriptProvider;
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
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.actions.executor.ActionExecutor;
import com.cognifide.cq.cqsm.core.progress.ProgressImpl;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingMode;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingPolicy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Script Manager Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
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

		actionFactory.update();
		LOG.info(String.format("Script execution started: %s [%s]", path, mode));
		Progress progress = new ProgressImpl(resolver.getUserID());
		final List<ActionDescriptor> descriptors = parseAllDescriptors(script, customDefinitions, resolver);
		final ActionExecutor actionExecutor = createExecutor(mode, resolver);
		final Context context = actionExecutor.getContext();
		final SessionSavingPolicy savingPolicy = context.getSavingPolicy();

		eventManager.trigger(Event.BEFORE_EXECUTE, script, mode, progress);

		for (ActionDescriptor descriptor : descriptors) {
			ActionResult result = actionExecutor.execute(descriptor);
			progress.addEntry(descriptor, result);

			if ((Status.ERROR == result.getStatus()) && (Mode.DRY_RUN != mode)) {
				eventManager.trigger(Event.AFTER_EXECUTE, script, mode, progress);
				return progress;
			}

			savingPolicy.save(context.getSession(), SessionSavingMode.EVERY_ACTION);
		}
		savingPolicy.save(context.getSession(), SessionSavingMode.SINGLE);

		eventManager.trigger(Event.AFTER_EXECUTE, script, mode, progress);
		return progress;
	}

	@Override
	public synchronized Progress process(final Script script, final Mode mode, ResourceResolver resolver)
			throws RepositoryException, PersistenceException {
		return process(script, mode, Maps.<String, String>newHashMap(), resolver);
	}

	public Progress process(Script script, final Mode mode, final Map<String, String> customDefinitions,
			ResourceResolver resolver) throws RepositoryException, PersistenceException {
		Progress progress;
		try {
			progress = execute(script, mode, customDefinitions, resolver);

		} catch (ExecutionException e) {
			progress = new ProgressImpl(resolver.getUserID());
			progress.addEntry(Message.getErrorMessage(e.getMessage()), Status.ERROR);
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

		if (Arrays.asList(Mode.DRY_RUN, Mode.RUN, Mode.AUTOMATIC_RUN).contains(mode)) {
			modifiableScript.setDryRunStatus(success);
		}

		if (mode.equals(Mode.VALIDATION)) {
			modifiableScript.setValid(success);
		}
	}

	@Override
	public Progress evaluate(String scriptContent, Mode mode, ResourceResolver resolver)
			throws RepositoryException, PersistenceException {
		return evaluate(scriptContent, mode, Maps.<String, String>newHashMap(), resolver);
	}

	public Progress evaluate(String scriptContent, Mode mode, Map<String, String> customDefinitions,
			ResourceResolver resolver) throws RepositoryException, PersistenceException {
		Script script = scriptFinder.find(ScriptManager.FILE_FOR_EVALUATION, false, resolver);
		if (script != null) {
			scriptStorage.remove(script, resolver);
		}

		InputStream stream = new ByteArrayInputStream(scriptContent.getBytes(StandardCharsets.UTF_8));
		script = scriptStorage.save(FILE_FOR_EVALUATION, stream, true, resolver);

		Progress progress = process(script, mode, customDefinitions, resolver);
		scriptStorage.remove(script, resolver);

		return progress;
	}

	@Override
	public Map<String, String> getPredefinedDefinitions() {
		if (predefinedDefinitions == null) {
			predefinedDefinitions = Collections.synchronizedMap(new TreeMap<String, String>());
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
		final List<Script> includes = new ArrayList<>();
		final HashMap<String, String> definitions = new HashMap<>();
		parseIncludeDescriptors(script, definitions, includes, resolver);
		return includes;
	}

	private List<ActionDescriptor> parseAllDescriptors(Script script, Map<String, String> customDefinitions,
			ResourceResolver resolver) throws ExecutionException {
		final List<Script> includes = new ArrayList<>();
		final HashMap<String, String> definitions = new HashMap<>();

		definitions.putAll(getPredefinedDefinitions());
		definitions.putAll(customDefinitions);

		return parseIncludeDescriptors(script, definitions, includes, resolver);
	}

	private List<ActionDescriptor> parseIncludeDescriptors(Script script, Map<String, String> definitions,
			List<Script> includes, ResourceResolver resolver) throws ExecutionException {
		final List<ActionDescriptor> descriptors = new LinkedList<>();
		LineIterator lineIterator = IOUtils.lineIterator(new StringReader(script.getData()));

		while (lineIterator.hasNext()) {
			String line = lineIterator.next();
			if (ScriptUtils.isAction(line)) {
				final String command = ScriptUtils.parseCommand(line, definitions);
				final ActionDescriptor descriptor = actionFactory.evaluate(command);
				final Action action = descriptor.getAction();

				descriptors.add(descriptor);

				if (action instanceof DefinitionProvider) {
					definitions.putAll(((DefinitionProvider) action).provideDefinitions(definitions));
				} else if (action instanceof ScriptProvider) {
					getIncludes(definitions, includes, resolver, descriptors, (ScriptProvider) action);
				}
			}
		}
		return descriptors;
	}

	private void getIncludes(Map<String, String> definitions, List<Script> includes,
			ResourceResolver resolver, List<ActionDescriptor> descriptors, ScriptProvider action)
			throws ExecutionException {
		for (String path : action.provideScripts()) {
			Script include = scriptFinder.find(path, resolver);

			if (include != null) {
				includes.add(include);
				descriptors.addAll(parseIncludeDescriptors(include, definitions, includes,
						resolver));
			} else {
				throw new ActionCreationException(
						String.format("Included script: '%s' does not exists.", path));
			}
		}
	}

	private ActionExecutor createExecutor(Mode mode, ResourceResolver resolver) throws RepositoryException {
		final Context context = new Context((JackrabbitSession) resolver.adaptTo(Session.class));
		return mode.getExecutor(context, actionFactory);
	}
}
