/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
