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

package com.cognifide.apm.core.grammar;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.core.grammar.ApmType.ApmEmpty;
import com.cognifide.apm.core.grammar.ApmType.ApmList;
import com.cognifide.apm.core.grammar.ApmType.ApmString;
import com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.AllowDenyCommandContext;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.BodyContext;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.DefineVariableContext;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.ForEachContext;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.GenericCommandContext;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.ImportScriptContext;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.RequireVariableContext;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.RunScriptContext;
import com.cognifide.apm.core.grammar.argument.ArgumentResolverException;
import com.cognifide.apm.core.grammar.argument.Arguments;
import com.cognifide.apm.core.grammar.common.Functions;
import com.cognifide.apm.core.grammar.executioncontext.ExecutionContext;
import com.cognifide.apm.core.grammar.parsedscript.InvalidSyntaxException;
import com.cognifide.apm.core.grammar.parsedscript.InvalidSyntaxMessageFactory;
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript;
import com.cognifide.apm.core.grammar.utils.ImportScript;
import com.cognifide.apm.core.grammar.utils.RequiredVariablesChecker;
import com.cognifide.apm.core.logger.Position;
import com.cognifide.apm.core.logger.Progress;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.sling.api.resource.ResourceResolver;

public class ScriptRunner {

  private final ScriptFinder scriptFinder;

  private final ResourceResolver resourceResolver;

  private final boolean validateOnly;

  private final ActionInvoker actionInvoker;

  public ScriptRunner(ScriptFinder scriptFinder, ResourceResolver resourceResolver, boolean validateOnly, ActionInvoker actionInvoker) {
    this.scriptFinder = scriptFinder;
    this.resourceResolver = resourceResolver;
    this.validateOnly = validateOnly;
    this.actionInvoker = actionInvoker;
  }

  public Progress execute(Script script, Progress progress, Map<String, String> initialDefinitions) {
    try {
      ExecutionContext executionContext = ExecutionContext.create(scriptFinder, resourceResolver, script, progress);
      initialDefinitions.forEach((name, value) -> executionContext.setVariable(name, new ApmString(value)));
      Executor executor = new Executor(executionContext);
      executor.visit(executionContext.getRoot().getApm());
    } catch (InvalidSyntaxException e) {
      List<String> errorMessages = InvalidSyntaxMessageFactory.detailedSyntaxError(e);
      progress.addEntry(Status.ERROR, errorMessages);
    } catch (ArgumentResolverException | ScriptExecutionException e) {
      progress.addEntry(Status.ERROR, e.getMessage());
    }
    return progress;
  }

  public Progress execute(Script script, Progress progress) {
    return execute(script, progress, Collections.emptyMap());
  }

  private class Executor extends ApmLangBaseVisitor<Status> {

    private final ExecutionContext executionContext;

    private Status globalResult;

    public Executor(ExecutionContext executionContext) {
      this.executionContext = executionContext;
      this.globalResult = Status.SUCCESS;
    }

    private boolean shouldVisitNextChild() {
      return globalResult != Status.ERROR;
    }

    @Override
    protected boolean shouldVisitNextChild(RuleNode node, Status currentResult) {
      return shouldVisitNextChild();
    }

    @Override
    protected Status aggregateResult(Status aggregate, Status nextResult) {
      globalResult = nextResult == Status.ERROR ? Status.ERROR : globalResult;
      return globalResult;
    }

    @Override
    public Status visitDefineVariable(DefineVariableContext ctx) {
      String variableName = ctx.IDENTIFIER().toString();
      ApmType variableValue = executionContext.resolveArgument(ctx.argument());
      executionContext.setVariable(variableName, variableValue);
      progress(ctx, Status.SUCCESS, "define", String.format("Defined variable: %s=%s", variableName, variableValue));
      return Status.SUCCESS;
    }

    @Override
    public Status visitRequireVariable(RequireVariableContext ctx) {
      String variableName = ctx.IDENTIFIER().toString();
      try {
        executionContext.getVariable(variableName);
      } catch (ArgumentResolverException e) {
        Status status = validateOnly ? Status.WARNING : Status.ERROR;
        progress(ctx, status, "require", String.format("Variable \"%s\" is required", variableName));
      }
      return Status.SUCCESS;
    }

    @Override
    public Status visitForEach(ForEachContext ctx) {
      List<Map<String, ApmType>> values = readValues(ctx);
      ListIterator<Map<String, ApmType>> iterator = values.listIterator();
      while (iterator.hasNext() && shouldVisitNextChild()) {
        try {
          int index = iterator.nextIndex();
          Map<String, ApmType> value = iterator.next();
          executionContext.createLocalContext();
          String valueStr = value.entrySet()
              .stream()
              .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
              .collect(Collectors.joining("\n"));
          progress(ctx, Status.SUCCESS, "for-each", String.format("%d. Begin: %s", index, valueStr));
          value.forEach(executionContext::setVariable);
          visit(ctx.body());
          progress(ctx, Status.SUCCESS, "for-each", String.format("%d. End", index));
        } finally {
          executionContext.removeLocalContext();
        }
      }
      return Status.SUCCESS;
    }

    @Override
    public Status visitRunScript(RunScriptContext ctx) {
      String path = Functions.getPath(ctx.path());
      Arguments arguments = executionContext.resolveArguments(ctx.namedArguments());
      ParsedScript loadScript = executionContext.loadScript(path);
      if (executionContext.scriptIsOnStack(loadScript)) {
        throw new ScriptExecutionException(String.format("Found cyclic reference to %s", loadScript.getPath()));
      }
      RequiredVariablesChecker.Result result = new RequiredVariablesChecker().checkNamedArguments(loadScript, arguments);
      if (result.isValid()) {
        executionContext.createScriptContext(loadScript);
        try {
          arguments.getNamed().forEach(executionContext::setVariable);
          progress(ctx, Status.SUCCESS, "run", String.format("Begin: path=%s", loadScript.getPath()), arguments);
          visit(loadScript.getApm());
          progress(ctx, Status.SUCCESS, "run", "End");
        } finally {
          executionContext.removeScriptContext();
        }
      } else {
        progress(ctx, Status.ERROR, "run", result.toMessages(), arguments);
      }
      return Status.SUCCESS;
    }

    @Override
    public Status visitGenericCommand(GenericCommandContext ctx) {
      String commandName = Functions.getIdentifier(ctx.commandName().identifier()).toUpperCase();
      Arguments arguments = executionContext.resolveArguments(ctx.complexArguments());
      return visitGenericCommand(ctx, commandName, arguments, ctx.body());
    }

    @Override
    public Status visitAllowDenyCommand(AllowDenyCommandContext ctx) {
      String commandName = ctx.ALLOW() != null ? "ALLOW" : "DENY";
      ApmType argument = executionContext.resolveArgument(ctx.argument());
      Arguments arguments = executionContext.resolveArguments(ctx.complexArguments());
      List<ApmType> required = new ArrayList<>(arguments.getRequired());
      if (ctx.ON() == null) {
        required.add(0, argument);
      } else {
        required.add(argument);
      }
      Arguments newArguments = new Arguments(required, arguments.getNamed(), arguments.getFlags());
      return visitGenericCommand(ctx, commandName, newArguments, null);
    }

    private Status visitGenericCommand(ParserRuleContext ctx, String commandName, Arguments arguments, BodyContext body) {
      if (validateOnly) {
        return visitGenericCommandValidateMode(ctx, commandName, arguments, body);
      } else {
        return visitGenericCommandRunMode(ctx, commandName, arguments, body);
      }
    }

    private Status visitGenericCommandRunMode(ParserRuleContext ctx, String commandName, Arguments arguments, BodyContext body) {
      try {
        if (body != null) {
          executionContext.createLocalContext();
        }
        Status status = actionInvoker.runAction(executionContext, commandName, arguments);
        if (status == Status.SUCCESS && body != null) {
          visit(body);
        }
        return status;
      } catch (ArgumentResolverException e) {
        progress(ctx, Status.ERROR, commandName, String.format("Action failed: %s", e.getMessage()));
      } finally {
        if (body != null) {
          executionContext.removeLocalContext();
        }
      }
      return Status.ERROR;
    }

    private Status visitGenericCommandValidateMode(ParserRuleContext ctx, String commandName, Arguments arguments, BodyContext body) {
      try {
        if (body != null) {
          executionContext.createLocalContext();
        }
        try {
          actionInvoker.runAction(executionContext, commandName, arguments);
        } catch (ArgumentResolverException e) {
          progress(ctx, Status.WARNING, commandName, String.format("Couldn't invoke action: %s", e.getMessage()));
        }
        if (body != null) {
          visit(body);
        }
      } finally {
        if (body != null) {
          executionContext.removeLocalContext();
        }
      }
      return Status.SUCCESS;
    }

    @Override
    public Status visitImportScript(ImportScriptContext ctx) {
      ImportScript.Result result = new ImportScript(executionContext).importScript(ctx);
      executionContext.getVariableHolder().setAll(result.getVariableHolder());
      progress(ctx, Status.SUCCESS, "import", result.toMessages());
      return Status.SUCCESS;
    }

    private List<Map<String, ApmType>> readValues(ForEachContext ctx) {
      String key = ctx.IDENTIFIER().toString();
      ApmType variableValue = executionContext.resolveArgument(ctx.argument());
      List<ApmType> values;
      if (variableValue instanceof ApmList) {
        values = variableValue.getList();
      } else if (variableValue instanceof ApmEmpty) {
        values = Collections.emptyList();
      } else {
        values = ImmutableList.of(variableValue);
      }
      return values.stream()
          .map(value -> ImmutableMap.of(key, value))
          .collect(Collectors.toList());
    }

    private void progress(ParserRuleContext ctx, Status status, String command, String detail) {
      progress(ctx, status, command, detail, null);
    }

    private void progress(ParserRuleContext ctx, Status status, String command, String detail, Arguments arguments) {
      progress(ctx, status, command, ImmutableList.of(detail), arguments);
    }

    private void progress(ParserRuleContext ctx, Status status, String command, List<String> details) {
      progress(ctx, status, command, details, null);
    }

    private void progress(ParserRuleContext ctx, Status status, String command, List<String> details, Arguments arguments) {
      executionContext.getProgress().addEntry(status, details, command, "", arguments, new Position(ctx.start.getLine()));
    }
  }
}
