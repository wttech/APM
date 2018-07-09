package com.cognifide.cq.cqsm.core.macro;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MacroRegister {

  private final Map<String, MacroDefinition> macros = new HashMap<>();

  void put(String name, MacroDefinition macroDefinition) {
    macros.put(name, macroDefinition);
  }

  public MacroDefinition get(String name) {
    return macros.get(name);
  }

  public Set<String> keySet() {
    return macros.keySet();
  }
}
