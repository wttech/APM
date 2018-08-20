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
