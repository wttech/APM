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

package com.cognifide.apm.core.grammar.utils;

import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.ApmType.ApmMap;
import com.cognifide.apm.core.grammar.ScriptExecutionException;
import com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser;
import com.cognifide.apm.core.grammar.argument.ArgumentResolver;
import com.cognifide.apm.core.grammar.common.Functions;
import com.cognifide.apm.core.grammar.datasource.DataSourceInvoker;
import com.cognifide.apm.core.grammar.executioncontext.ExecutionContext;
import com.cognifide.apm.core.grammar.executioncontext.VariableHolder;
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

public class ImportScript {

  private final ExecutionContext executionContext;

  private final ResourceResolver resourceResolver;

  private final DataSourceInvoker dataSourceInvoker;

  private final Set<ParsedScript> visitedScripts;

  public ImportScript(ExecutionContext executionContext, ResourceResolver resourceResolver, DataSourceInvoker dataSourceInvoker) {
    this.executionContext = executionContext;
    this.resourceResolver = resourceResolver;
    this.dataSourceInvoker = dataSourceInvoker;
    this.visitedScripts = new HashSet<>();
  }

  public Result importScript(ApmLangParser.ImportScriptContext ctx) {
    String path = Functions.getPath(ctx.path());
    ImportScriptVisitor importScriptVisitor = new ImportScriptVisitor();
    importScriptVisitor.visit(ctx);
    return new Result(path, importScriptVisitor.variableHolder);
  }

  private String getNamespace(ApmLangParser.ImportScriptContext ctx) {
    return ctx.name() != null ? ctx.name().IDENTIFIER().toString() : "";
  }

  private class ImportScriptVisitor extends ApmLangBaseVisitor<Object> {

    private final VariableHolder variableHolder;

    private final ArgumentResolver argumentResolver;

    public ImportScriptVisitor() {
      this.variableHolder = new VariableHolder();
      this.argumentResolver = new ArgumentResolver(variableHolder, resourceResolver, dataSourceInvoker);
    }

    @Override
    public Object visitDefineVariable(ApmLangParser.DefineVariableContext ctx) {
      String variableName = ctx.IDENTIFIER().toString();
      ApmType variableValue = argumentResolver.resolve(ctx.argument());
      variableHolder.set(variableName, variableValue);
      return null;
    }

    @Override
    public Object visitImportScript(ApmLangParser.ImportScriptContext ctx) {
      String path = Functions.getPath(ctx.path());
      String namespace = getNamespace(ctx);
      ImportScriptVisitor importScriptVisitor = new ImportScriptVisitor();
      ParsedScript parsedScript = executionContext.loadScript(path);

      if (!visitedScripts.contains(parsedScript)) {
        visitedScripts.add(parsedScript);
      } else {
        throw new ScriptExecutionException(String.format("Found cyclic reference to %s", parsedScript.getPath()));
      }

      importScriptVisitor.visit(parsedScript.getApm());
      VariableHolder scriptVariableHolder = importScriptVisitor.variableHolder;
      if (StringUtils.isEmpty(namespace)) {
        variableHolder.setAll(scriptVariableHolder);
      } else {
        variableHolder.set(namespace, new ApmMap(scriptVariableHolder.toMap()));
      }
      return null;
    }
  }

  public static class Result {

    private final String path;

    private final VariableHolder variableHolder;

    public Result(String path, VariableHolder variableHolder) {
      this.path = path;
      this.variableHolder = variableHolder;
    }

    public VariableHolder getVariableHolder() {
      return variableHolder;
    }

    public List<String> toMessages() {
      List<String> resultMessagesPrefix = Collections.singletonList(String.format("Import from script %s. Notice, only DEFINE actions were processed!", path));
      List<String> importedVariables = variableHolder.toMap()
          .entrySet()
          .stream()
          .map(entry -> String.format("Imported variable: %s=%s", entry.getKey(), entry.getValue()))
          .collect(Collectors.toList());
      return ListUtils.union(resultMessagesPrefix, importedVariables);
    }
  }
}
