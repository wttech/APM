/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.apm.core.grammar.executioncontext;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.ScriptExecutionException;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser;
import com.cognifide.apm.core.grammar.argument.ArgumentResolver;
import com.cognifide.apm.core.grammar.argument.Arguments;
import com.cognifide.apm.core.grammar.common.StackWithRoot;
import com.cognifide.apm.core.grammar.datasource.DataSourceInvoker;
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript;
import com.cognifide.apm.core.logger.Progress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.ResourceResolver;

public class ExecutionContext implements ExternalExecutionContext {

  private final ScriptFinder scriptFinder;

  private final ResourceResolver resourceResolver;

  private final DataSourceInvoker dataSourceInvoker;

  private final ParsedScript root;

  private final Progress progress;

  private final Map<String, ParsedScript> parsedScripts;

  private final StackWithRoot<RunScript> runScripts;

  private ExecutionContext(ScriptFinder scriptFinder, ResourceResolver resourceResolver, DataSourceInvoker dataSourceInvoker, ParsedScript root, Progress progress) {
    this.scriptFinder = scriptFinder;
    this.resourceResolver = resourceResolver;
    this.dataSourceInvoker = dataSourceInvoker;
    this.root = root;
    this.progress = progress;
    this.parsedScripts = new HashMap<>();
    this.runScripts = new StackWithRoot<>(new RunScript(root));
    registerScript(root);
  }

  public static ExecutionContext create(ScriptFinder scriptFinder, ResourceResolver resourceResolver, DataSourceInvoker dataSourceInvoker, Script script, Progress progress) {
    return new ExecutionContext(scriptFinder, resourceResolver, dataSourceInvoker, ParsedScript.create(script), progress);
  }

  public ParsedScript getRoot() {
    return root;
  }

  private RunScript getCurrentRunScript() {
    return runScripts.peek();
  }

  public VariableHolder getVariableHolder() {
    return getCurrentRunScript().getVariableHolder();
  }

  private ArgumentResolver getArgumentResolver() {
    return new ArgumentResolver(getVariableHolder(), resourceResolver, dataSourceInvoker);
  }

  public ParsedScript loadScript(String path) {
    String absolutePath = resolveAbsolutePath(path);
    return parsedScripts.computeIfAbsent(absolutePath, this::fetchScript);
  }

  public boolean scriptIsOnStack(ParsedScript parsedScript) {
    return StreamSupport.stream(runScripts.spliterator(), false)
        .anyMatch(runScript -> Objects.equals(runScript.getParsedScript(), parsedScript));
  }

  public void createScriptContext(ParsedScript parsedScript) {
    runScripts.push(new RunScript(parsedScript));
  }

  public void createLocalContext() {
    getVariableHolder().createLocalContext();
  }

  public void removeScriptContext() {
    runScripts.pop();
  }

  public void removeLocalContext() {
    getVariableHolder().removeLocalContext();
  }

  @Override
  public Progress getProgress() {
    return progress;
  }

  @Override
  public void setVariable(String key, ApmType value) {
    getVariableHolder().set(key, value);
  }

  @Override
  public ApmType getVariable(String key) {
    return getVariableHolder().get(key);
  }

  @Override
  public void setAuthorizable(Authorizable authorizable) {
    getVariableHolder().setAuthorizable(authorizable);
  }

  @Override
  public Authorizable getAuthorizable() {
    return getVariableHolder().getAuthorizable();
  }

  public Arguments resolveArguments(ApmLangParser.ComplexArgumentsContext arguments) {
    return getArgumentResolver().resolve(arguments);
  }

  public Arguments resolveArguments(ApmLangParser.NamedArgumentsContext arguments) {
    return getArgumentResolver().resolve(arguments);
  }

  public ApmType resolveArgument(ApmLangParser.ArgumentContext argument) {
    return getArgumentResolver().resolve(argument);
  }

  private ParsedScript fetchScript(String path) {
    Script script = Optional.ofNullable(scriptFinder.find(path, resourceResolver))
        .orElseThrow(() -> new ScriptExecutionException(String.format("Script not found %s", path)));
    return ParsedScript.create(script);
  }

  private void registerScript(ParsedScript parsedScript) {
    parsedScripts.put(parsedScript.getPath(), parsedScript);
  }

  private String resolveAbsolutePath(String path) {
    if (path.startsWith("/")) {
      return path;
    } else {
      return StringUtils.substringBeforeLast(runScripts.peek().getPath(), "/") + "/" + path;
    }
  }
}
