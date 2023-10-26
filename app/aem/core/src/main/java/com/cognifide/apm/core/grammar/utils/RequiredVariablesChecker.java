/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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

package com.cognifide.apm.core.grammar.utils;

import com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser;
import com.cognifide.apm.core.grammar.argument.Arguments;
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequiredVariablesChecker {

  public Result checkNamedArguments(ParsedScript parsedScript, Arguments arguments) {
    RequireVariableVisitor requireVariableVisitor = new RequireVariableVisitor();
    requireVariableVisitor.visit(parsedScript.getApm());
    List<String> missingNamedArguments = requireVariableVisitor.variables
        .stream()
        .filter(variable -> !arguments.getNamed().containsKey(variable))
        .collect(Collectors.toList());
    return new Result(missingNamedArguments);
  }

  private static class RequireVariableVisitor extends ApmLangBaseVisitor<Object> {

    private final List<String> variables;

    public RequireVariableVisitor() {
      this.variables = new ArrayList<>();
    }

    @Override
    public Object visitRequireVariable(ApmLangParser.RequireVariableContext ctx) {
      variables.add(ctx.IDENTIFIER().toString());
      return null;
    }
  }

  public static class Result {

    private final List<String> missingNamedArguments;

    public Result(List<String> missingNamedArguments) {
      this.missingNamedArguments = missingNamedArguments;
    }

    public boolean isValid() {
      return missingNamedArguments.isEmpty();
    }

    public List<String> toMessages() {
      return missingNamedArguments.stream()
          .map(parameter -> String.format("Parameter \"%s\" is required", parameter))
          .collect(Collectors.toList());
    }
  }
}
