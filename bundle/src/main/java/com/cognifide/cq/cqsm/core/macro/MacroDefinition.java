package com.cognifide.cq.cqsm.core.macro;

import com.cognifide.apm.antlr.ApmLangParser.BodyContext;
import com.cognifide.apm.antlr.ApmLangParser.MacroDefinitionContext;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class MacroDefinition {

  private final String name;
  private final MacroDefinitionContext macro;

  private MacroDefinition(String name, MacroDefinitionContext macro) {
    this.name = name;
    this.macro = macro;
  }

  public static MacroDefinition of(MacroDefinitionContext macro) {
    String name = macro.name().IDENTIFIER().toString();
    return new MacroDefinition(name, macro);
  }

  public String getName() {
    return name;
  }

  public MacroDefinitionContext getMacro() {
    return macro;
  }

  public BodyContext getBody() {
    return macro.body();
  }

  public List<String> getParametersNames() {
    if (macro.parametersDefinition() != null) {
      return macro.parametersDefinition().IDENTIFIER().stream()
          .map(Object::toString)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
