package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.core.antlr.parameter.ParameterResolver;
import com.cognifide.cq.cqsm.core.loader.ScriptTree;
import com.cognifide.cq.cqsm.core.macro.MacroRegister;
import com.cognifide.cq.cqsm.core.progress.ProgressImpl;
import lombok.Getter;

@Getter
public class ScriptContext {

  private final VariableHolder variableHolder = VariableHolder.empty();
  private final ParameterResolver parameterResolver = new ParameterResolver(variableHolder);
  private final MacroRegister macroRegister;
  private final ScriptTree scriptTree;
  private final Progress progress;

  public ScriptContext(String executor, MacroRegister macroRegister, ScriptTree scriptTree) {
    this.macroRegister = macroRegister;
    this.scriptTree = scriptTree;
    this.progress = new ProgressImpl(executor);
  }
}
