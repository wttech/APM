package com.cognifide.cq.cqsm.core.macro;

import com.cognifide.apm.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.antlr.ApmLangParser.ApmContext;
import com.cognifide.apm.antlr.ApmLangParser.MacroDefinitionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MacroRegistrar {

  public MacroRegister findMacroDefinitions(MacroRegister register, ApmContext script) {
    MacroFinder finder = new MacroFinder();
    List<MacroDefinitionContext> macroDefinitions = finder.visit(script);
    for (MacroDefinitionContext macroDefinition : macroDefinitions) {
      MacroDefinition macro = MacroDefinition.of(macroDefinition);
      register.put(macro.getName(), macro);
    }
    return register;
  }

  private static class MacroFinder extends ApmLangBaseVisitor<List<MacroDefinitionContext>> {

    @Override
    protected List<MacroDefinitionContext> defaultResult() {
      return new ArrayList<>();
    }

    @Override
    protected List<MacroDefinitionContext> aggregateResult(List<MacroDefinitionContext> aggregate,
        List<MacroDefinitionContext> nextResult) {
      if (nextResult != null) {
        aggregate.addAll(nextResult);
      }
      return aggregate;
    }

    @Override
    public List<MacroDefinitionContext> visitMacroDefinition(MacroDefinitionContext ctx) {
      return Collections.singletonList(ctx);
    }
  }
}
