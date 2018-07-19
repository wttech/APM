package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.apm.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.antlr.ApmLangParser.ApmContext;
import com.cognifide.apm.antlr.ApmLangParser.GenericCommandContext;
import com.cognifide.apm.antlr.ApmLangParser.MacroDefinitionContext;
import com.cognifide.apm.antlr.ApmLangParser.MacroExecutionContext;
import com.cognifide.apm.antlr.ApmLangParser.ParameterContext;
import com.cognifide.apm.antlr.ApmLangParser.ScriptInclusionContext;
import com.cognifide.cq.cqsm.core.antlr.parameter.ParameterResolver;
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import com.cognifide.cq.cqsm.core.loader.ScriptInclusion;
import com.cognifide.cq.cqsm.core.loader.ScriptTree;
import com.cognifide.cq.cqsm.core.macro.MacroDefinition;
import com.cognifide.cq.cqsm.core.macro.MacroExecution;
import com.cognifide.cq.cqsm.core.macro.MacroRegister;
import com.cognifide.cq.cqsm.core.macro.MacroRegistrar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;

public class ScriptRunner {

  private final ActionInvoker actionInvoker;

  public ScriptRunner(ActionInvoker actionInvoker) {
    this.actionInvoker = actionInvoker;
  }

  public List<String> execute(ScriptTree scriptTree) {
    MacroRegister macroRegister = buildMacroRegister(scriptTree);
    ScriptContext scriptContext = new ScriptContext(macroRegister, scriptTree);
    Executor executor = new Executor(actionInvoker, scriptContext);
    return executor.visit(scriptTree.getRoot());
  }

  private MacroRegister buildMacroRegister(ScriptTree scriptTree) {
    MacroRegistrar macroRegistrar = new MacroRegistrar();
    MacroRegister macroRegister = macroRegistrar.findMacroDefinitions(new MacroRegister(), scriptTree.getRoot());
    for (ApmContext reference : scriptTree.getIncludedScripts()) {
      macroRegister = macroRegistrar.findMacroDefinitions(macroRegister, reference);
    }
    return macroRegister;
  }

  private static class Executor extends ApmLangBaseVisitor<List<String>> {

    private final ActionInvoker actionInvoker;
    private final ScriptContext scriptContext;

    private Executor(ActionInvoker actionInvoker, ScriptContext scriptContext) {
      this.actionInvoker = actionInvoker;
      this.scriptContext = scriptContext;
    }

    @Override
    protected List<String> defaultResult() {
      return new ArrayList<>();
    }

    @Override
    protected List<String> aggregateResult(List<String> aggregate, List<String> nextResult) {
      if (nextResult != null) {
        aggregate.addAll(nextResult);
      }
      return aggregate;
    }

    @Override
    public List<String> visitScriptInclusion(ScriptInclusionContext ctx) {
      VariableHolder variableHolder = scriptContext.getVariableHolder();
      try {
        variableHolder.createLocalContext();
        String referencePath = ScriptInclusion.of(ctx).getPath();
        ApmContext includedScript = scriptContext.getScriptTree().getIncludedScript(referencePath);
        return visit(includedScript);
      } finally {
        variableHolder.removeLocalContext();
      }
    }

    @Override
    public List<String> visitMacroDefinition(MacroDefinitionContext ctx) {
      return Collections.emptyList();
    }

    @Override
    public List<String> visitMacroExecution(MacroExecutionContext ctx) {
      ParameterResolver parameterResolver = scriptContext.getParameterResolver();
      VariableHolder variableHolder = scriptContext.getVariableHolder();
      try {
        variableHolder.createLocalContext();
        MacroExecution commandUseMacro = MacroExecution.of(ctx);
        MacroDefinition macroDefinition = scriptContext.getMacroRegister().get(commandUseMacro.getName());
        List<String> parametersNames = macroDefinition.getParametersNames();
        List<ParameterContext> parameters = commandUseMacro.getParameters();
        for (int i = 0; i < Math.min(parametersNames.size(), parameters.size()); i++) {
          ApmType value = parameterResolver.resolve(parameters.get(i));
          variableHolder.put(parametersNames.get(i), value);
        }
        return visit(macroDefinition.getBody());
      } finally {
        variableHolder.removeLocalContext();
      }
    }

    @Override
    public List<String> visitGenericCommand(GenericCommandContext ctx) {
      String commandName = ctx.IDENTIFIER().toString();
      String command = ctx.children.stream()
          .map(ParseTree::getText)
          .collect(Collectors.joining(" "));
      ParameterResolver parameterResolver = scriptContext.getParameterResolver();
      List<ApmType> parameters = ctx.parameter().stream()
          .map(parameterResolver::resolve)
          .collect(Collectors.toList());
      return actionInvoker.runAction(ctx, command, commandName, new Parameters(parameters));
    }
  }
}
