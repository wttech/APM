/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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

package com.cognifide.cq.cqsm.core.antlr;

import static java.lang.String.format;

import com.cognifide.apm.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.antlr.ApmLangParser.ApmContext;
import com.cognifide.apm.antlr.ApmLangParser.ForeachContext;
import com.cognifide.apm.antlr.ApmLangParser.GenericCommandContext;
import com.cognifide.apm.antlr.ApmLangParser.MacroDefinitionContext;
import com.cognifide.apm.antlr.ApmLangParser.MacroExecutionContext;
import com.cognifide.apm.antlr.ApmLangParser.ParameterContext;
import com.cognifide.apm.antlr.ApmLangParser.ScriptInclusionContext;
import com.cognifide.apm.antlr.ApmLangParser.VariableDefinitionContext;
import com.cognifide.cq.cqsm.api.logger.Message;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.logger.Status;
import com.cognifide.cq.cqsm.core.antlr.parameter.ParameterResolver;
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import com.cognifide.cq.cqsm.core.antlr.type.ApmValue;
import com.cognifide.cq.cqsm.core.loader.ScriptInclusion;
import com.cognifide.cq.cqsm.core.macro.MacroDefinition;
import com.cognifide.cq.cqsm.core.macro.MacroExecution;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptRunner {

  private final ActionInvoker actionInvoker;

  public ScriptRunner(ActionInvoker actionInvoker) {
    this.actionInvoker = actionInvoker;
  }

  public Progress execute(ScriptContext scriptContext) {
    Executor executor = new Executor(scriptContext);
    executor.visit(scriptContext.getScriptTree().getRoot());
    return scriptContext.getProgress();
  }

  private class Executor extends ApmLangBaseVisitor<Void> {

    private final ScriptContext scriptContext;

    private Executor(ScriptContext scriptContext) {
      this.scriptContext = scriptContext;
    }

    @Override
    public Void visitScriptInclusion(ScriptInclusionContext ctx) {
      VariableHolder variableHolder = scriptContext.getVariableHolder();
      try {
        variableHolder.createIsolatedLocalContext();
        String referencePath = ScriptInclusion.of(ctx).getPath();
        ApmContext includedScript = scriptContext.getScriptTree().getIncludedScript(referencePath);
        visit(includedScript);
      } finally {
        variableHolder.removeLocalContext();
      }
      return null;
    }

    @Override
    public Void visitVariableDefinition(VariableDefinitionContext ctx) {
      ParameterResolver parameterResolver = scriptContext.getParameterResolver();
      VariableHolder variableHolder = scriptContext.getVariableHolder();
      String variableName = ctx.IDENTIFIER().toString();
      ApmType variableValue = parameterResolver.resolve(ctx.parameter());
      variableHolder.put(variableName, variableValue);
      return null;
    }

    @Override
    public Void visitForeach(ForeachContext ctx) {
      ParameterResolver parameterResolver = scriptContext.getParameterResolver();
      VariableHolder variableHolder = scriptContext.getVariableHolder();
      try {
        variableHolder.createLocalContext();
        String variableName = ctx.IDENTIFIER().toString();
        ApmType variableValue = parameterResolver.resolve(ctx.parameter());
        List<ApmValue> values;
        if (variableValue.isApmList()) {
          values = variableValue.getList();
        } else {
          values = Collections.singletonList((ApmValue) variableValue);
        }
        int i = 1;
        info("foreach: begin");
        for (ApmValue value : values) {
          info(format("iteration: %d", i));
          variableHolder.put(variableName, value);
          visit(ctx.body());
          i++;
        }
        info("foreach: end");
      } finally {
        variableHolder.removeLocalContext();
      }
      return null;
    }

    @Override
    public Void visitMacroDefinition(MacroDefinitionContext ctx) {
      return null;
    }

    @Override
    public Void visitMacroExecution(MacroExecutionContext ctx) {
      ParameterResolver parameterResolver = scriptContext.getParameterResolver();
      VariableHolder variableHolder = scriptContext.getVariableHolder();
      try {
        variableHolder.createIsolatedLocalContext();
        MacroExecution commandUseMacro = MacroExecution.of(ctx);
        MacroDefinition macroDefinition = scriptContext.getMacroRegister().get(commandUseMacro.getName());
        List<String> parametersNames = macroDefinition.getParametersNames();
        List<ParameterContext> parameters = commandUseMacro.getParameters();
        for (int i = 0; i < Math.min(parametersNames.size(), parameters.size()); i++) {
          ApmType value = parameterResolver.resolve(parameters.get(i));
          variableHolder.put(parametersNames.get(i), value);
        }
        visit(macroDefinition.getBody());
      } finally {
        variableHolder.removeLocalContext();
      }
      return null;
    }

    @Override
    public Void visitGenericCommand(GenericCommandContext ctx) {
      String commandName = ctx.IDENTIFIER().toString().toUpperCase();
      ParameterResolver parameterResolver = scriptContext.getParameterResolver();
      List<ApmType> parameters = ctx.parameter().stream()
          .map(parameterResolver::resolve)
          .collect(Collectors.toList());
      actionInvoker.runAction(scriptContext.getProgress(), commandName, new Parameters(parameters));
      return null;
    }

    private void info(String shortInfo) {
      info(shortInfo, "");
    }

    private void info(String shortInfo, String details) {
      scriptContext.getProgress().addEntry(shortInfo, Message.getInfoMessage(details), Status.SUCCESS);
    }
  }
}
