package com.cognifide.cq.cqsm.core.macro;

import com.cognifide.apm.antlr.ApmLangParser.MacroExecutionContext;
import com.cognifide.apm.antlr.ApmLangParser.ParameterContext;
import java.util.Collections;
import java.util.List;

public final class MacroExecution {

  private final String name;
  private final MacroExecutionContext macro;

  private MacroExecution(String name, MacroExecutionContext macro) {
    this.name = name;
    this.macro = macro;
  }

  public static MacroExecution of(MacroExecutionContext macro) {
    String name = macro.name().IDENTIFIER().toString();
    return new MacroExecution(name, macro);
  }

  public String getName() {
    return name;
  }

  public MacroExecutionContext getMacro() {
    return macro;
  }

  public List<ParameterContext> getParameters() {
    if (macro.parametersInvocation() != null) {
      return macro.parametersInvocation().parameter();
    }
    return Collections.emptyList();
  }
}
