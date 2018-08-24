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

package com.cognifide.cq.cqsm.core.antlr.parameter;

import com.cognifide.apm.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.antlr.ApmLangParser;
import com.cognifide.apm.antlr.ApmLangParser.ArrayContext;
import com.cognifide.apm.antlr.ApmLangParser.BooleanValueContext;
import com.cognifide.apm.antlr.ApmLangParser.NullValueContext;
import com.cognifide.apm.antlr.ApmLangParser.NumberValueContext;
import com.cognifide.apm.antlr.ApmLangParser.ParameterContext;
import com.cognifide.apm.antlr.ApmLangParser.StringConstContext;
import com.cognifide.apm.antlr.ApmLangParser.StringValueContext;
import com.cognifide.cq.cqsm.core.antlr.StringLiteral;
import com.cognifide.cq.cqsm.core.antlr.VariableHolder;
import com.cognifide.cq.cqsm.core.antlr.type.ApmBoolean;
import com.cognifide.cq.cqsm.core.antlr.type.ApmList;
import com.cognifide.cq.cqsm.core.antlr.type.ApmNull;
import com.cognifide.cq.cqsm.core.antlr.type.ApmNumber;
import com.cognifide.cq.cqsm.core.antlr.type.ApmString;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import com.cognifide.cq.cqsm.core.antlr.type.ApmValue;
import com.google.common.primitives.Ints;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParameterResolver {

  private final VariableHolder variableHolder;
  private final Resolver resolver;

  public ParameterResolver(VariableHolder variableHolder) {
    this.variableHolder = variableHolder;
    this.resolver = new Resolver();
  }

  public ApmType resolve(ParameterContext context) {
    return resolver.visit(context);
  }

  private class Resolver extends ApmLangBaseVisitor<ApmType> {

    @Override
    protected ApmType defaultResult() {
      return new ApmNull();
    }

    @Override
    public ApmType visitArray(ArrayContext ctx) {
      List<ApmValue> values = Optional.ofNullable(ctx.children)
          .orElse(Collections.emptyList())
          .stream()
          .map(child -> child.accept(this))
          .filter(ApmValue.class::isInstance)
          .map(ApmValue.class::cast)
          .collect(Collectors.toList());
      return new ApmList(values);
    }

    @Override
    public ApmType visitBooleanValue(BooleanValueContext ctx) {
      return new ApmBoolean(Boolean.parseBoolean(ctx.getText()));
    }

    @Override
    public ApmType visitNullValue(NullValueContext ctx) {
      return new ApmNull();
    }

    @Override
    public ApmType visitNumberValue(NumberValueContext ctx) {
      return new ApmNumber(Ints.tryParse(ctx.NUMBER_LITERAL().toString()));
    }

    @Override
    public ApmType visitStringValue(StringValueContext ctx) {
      return new ApmString(StringLiteral.getValue(ctx.STRING_LITERAL()));
    }

    @Override
    public ApmType visitStringConst(StringConstContext ctx) {
      return new ApmString(ctx.IDENTIFIER().toString());
    }

    @Override
    public ApmType visitVariable(ApmLangParser.VariableContext ctx) {
      return variableHolder.get(ctx.IDENTIFIER().toString());
    }

  }
}
