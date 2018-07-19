package com.cognifide.cq.cqsm.core.macro;

import com.cognifide.apm.antlr.ApmLangParser.ApmContext;
import com.cognifide.apm.antlr.ApmLangParser.MacroDefinitionContext;
import com.cognifide.cq.cqsm.core.antlr.ListBaseVisitor;
import java.util.Collections;
import java.util.List;

public class MacroRegistrar {

  public MacroRegister findMacroDefinitions(MacroRegister register, ApmContext script) {
    MacroDefinitionFinder finder = new MacroDefinitionFinder();
    List<MacroDefinitionContext> macroDefinitions = finder.visit(script);
    for (MacroDefinitionContext macroDefinition : macroDefinitions) {
      MacroDefinition macro = MacroDefinition.of(macroDefinition);
      register.put(macro.getName(), macro);
    }
    return register;
  }

  private static class MacroDefinitionFinder extends ListBaseVisitor<MacroDefinitionContext> {

    @Override
    public List<MacroDefinitionContext> visitMacroDefinition(MacroDefinitionContext ctx) {
      return Collections.singletonList(ctx);
    }
  }
}
