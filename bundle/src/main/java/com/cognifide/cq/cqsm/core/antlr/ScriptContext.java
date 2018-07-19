package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.cq.cqsm.core.antlr.parameter.ParameterResolver;
import com.cognifide.cq.cqsm.core.loader.ScriptTree;
import com.cognifide.cq.cqsm.core.macro.MacroRegister;

public class ScriptContext {

  private final VariableHolder variableHolder = VariableHolder.empty();
  private final ParameterResolver parameterResolver = new ParameterResolver(variableHolder);
  private final MacroRegister macroRegister;
  private final ScriptTree scriptTree;

  public ScriptContext(MacroRegister macroRegister, ScriptTree scriptTree) {
    this.macroRegister = macroRegister;
    this.scriptTree = scriptTree;
  }

  public ParameterResolver getParameterResolver() {
    return parameterResolver;
  }

  public VariableHolder getVariableHolder() {
    return variableHolder;
  }

  public MacroRegister getMacroRegister() {
    return macroRegister;
  }

  public ScriptTree getScriptTree() {
    return scriptTree;
  }
}
