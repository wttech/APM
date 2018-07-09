package com.cognifide.cq.cqsm.core.macro;

import com.cognifide.apm.antlr.ApmLangParser.CommandUseMacroContext;
import com.cognifide.apm.antlr.ApmLangParser.ParameterContext;
import java.util.Collections;
import java.util.List;

public final class MacroExecution {

  private final String name;
  private final CommandUseMacroContext macro;

  private MacroExecution(String name, CommandUseMacroContext macro) {
    this.name = name;
    this.macro = macro;
  }

  public static MacroExecution of(CommandUseMacroContext macro) {
    String name = macro.name().IDENTIFIER().toString();
    return new MacroExecution(name, macro);
  }

  public String getName() {
    return name;
  }

  public CommandUseMacroContext getMacro() {
    return macro;
  }

  public List<ParameterContext> getParameters() {
    if (macro.parametersInvokation() != null) {
      return macro.parametersInvokation().parameter();
    }
    return Collections.emptyList();
  }
}
