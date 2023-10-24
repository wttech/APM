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

package com.cognifide.apm.core.grammar.argument;

import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.ApmType.ApmEmpty;
import com.cognifide.apm.core.grammar.ApmType.ApmInteger;
import com.cognifide.apm.core.grammar.ApmType.ApmList;
import com.cognifide.apm.core.grammar.ApmType.ApmMap;
import com.cognifide.apm.core.grammar.ApmType.ApmPair;
import com.cognifide.apm.core.grammar.ApmType.ApmString;
import com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser;
import com.cognifide.apm.core.grammar.common.Functions;
import com.cognifide.apm.core.grammar.executioncontext.VariableHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;

public class ArgumentResolver {

  private final VariableHolder variableHolder;

  private final SingleArgumentResolver singleArgumentResolver;

  public ArgumentResolver(VariableHolder variableHolder) {
    this.variableHolder = variableHolder;
    this.singleArgumentResolver = new SingleArgumentResolver();
  }

  public Arguments resolve(ApmLangParser.ComplexArgumentsContext context) {
    if (context != null) {
      MultiArgumentResolver multiArgumentResolver = new MultiArgumentResolver();
      multiArgumentResolver.visitComplexArguments(context);
      return new Arguments(multiArgumentResolver.required, multiArgumentResolver.named, multiArgumentResolver.flags);
    } else {
      return new Arguments();
    }
  }

  public Arguments resolve(ApmLangParser.NamedArgumentsContext context) {
    if (context != null) {
      MultiArgumentResolver multiArgumentResolver = new MultiArgumentResolver();
      multiArgumentResolver.visitNamedArguments(context);
      return new Arguments(multiArgumentResolver.required, multiArgumentResolver.named, multiArgumentResolver.flags);
    } else {
      return new Arguments();
    }
  }

  public ApmType resolve(ApmLangParser.ArgumentContext context) {
    if (context != null) {
      return singleArgumentResolver.visitArgument(context);
    } else {
      return new ApmEmpty();
    }
  }

  private class MultiArgumentResolver extends ApmLangBaseVisitor<Object> {

    private final List<ApmType> required = new ArrayList<>();

    private final Map<String, ApmType> named = new HashMap<>();

    private final List<String> flags = new ArrayList<>();

    @Override
    public Object visitRequiredArgument(ApmLangParser.RequiredArgumentContext ctx) {
      required.add(singleArgumentResolver.visitArgument(ctx.argument()));
      return null;
    }

    @Override
    public Object visitNamedArgument(ApmLangParser.NamedArgumentContext ctx) {
      named.put(ctx.IDENTIFIER().toString(), singleArgumentResolver.visitArgument(ctx.argument()));
      return null;
    }

    @Override
    public Object visitFlag(ApmLangParser.FlagContext ctx) {
      flags.add(Functions.getIdentifier(ctx.identifier()));
      return null;
    }
  }

  private class SingleArgumentResolver extends ApmLangBaseVisitor<ApmType> {

    @Override
    protected ApmType defaultResult() {
      return new ApmEmpty();
    }

    @Override
    public ApmType visitArray(ApmLangParser.ArrayContext ctx) {
      List<ApmType> values = ctx.children
          .stream()
          .map(child -> child.accept(this))
          .filter(value -> !(value instanceof ApmEmpty))
          .collect(Collectors.toList());
      return new ApmList(values);
    }

    @Override
    public ApmType visitName(ApmLangParser.NameContext ctx) {
      return new ApmString(ctx.IDENTIFIER().toString());
    }

    @Override
    public ApmType visitPrivilegeName(ApmLangParser.PrivilegeNameContext ctx) {
      String value = ctx.children
          .stream()
          .map(Object::toString)
          .collect(Collectors.joining());
      return new ApmString(value);
    }

    @Override
    public ApmType visitStructure(ApmLangParser.StructureContext ctx) {
      Map<String, ApmType> values = ctx.children
          .stream()
          .map(child -> child.accept(this))
          .filter(ApmPair.class::isInstance)
          .map(ApmPair.class::cast)
          .collect(Collectors.toMap(
              ApmPair::getKey,
              ApmPair::getValue,
              (key, value) -> {
                throw new IllegalStateException(String.format("Duplicate key %s", key));
              },
              LinkedHashMap::new
          ));
      return new ApmMap(values);
    }

    @Override
    public ApmType visitStructureEntry(ApmLangParser.StructureEntryContext ctx) {
      String key = Functions.getKey(ctx.structureKey());
      return ctx.structureValue()
          .children
          .stream()
          .map(child -> child.accept(this))
          .map(value -> new ApmPair(key, value))
          .findFirst()
          .map(ApmType.class::cast)
          .orElse(new ApmEmpty());
    }

    @Override
    public ApmType visitExpression(ApmLangParser.ExpressionContext ctx) {
      if (ctx.plus() != null) {
        ApmType left = visit(ctx.expression(0));
        ApmType right = visit(ctx.expression(1));
        if (left instanceof ApmString && right instanceof ApmString) {
          return new ApmString(left.getString() + right.getString());
        } else if (left instanceof ApmString && right instanceof ApmInteger) {
          return new ApmString(left.getString() + right.getInteger().toString());
        } else if (left instanceof ApmInteger && right instanceof ApmString) {
          return new ApmString(left.getInteger().toString() + right.getString());
        } else if (left instanceof ApmInteger && right instanceof ApmInteger) {
          return new ApmInteger(left.getInteger() + right.getInteger());
        } else if (left instanceof ApmList && right instanceof ApmList) {
          return new ApmList(ListUtils.union(left.getList(), right.getList()));
        } else {
          throw new ArgumentResolverException(String.format("Operation not supported for given values %s and %s", left, right));
        }
      } else if (ctx.value() != null) {
        return visit(ctx.value());
      } else {
        return super.visitExpression(ctx);
      }
    }

    @Override
    public ApmType visitNumberValue(ApmLangParser.NumberValueContext ctx) {
      String value = ctx.NUMBER_LITERAL().toString();
      try {
        int number = Integer.parseInt(value);
        return new ApmInteger(number);
      } catch (NumberFormatException e) {
        throw new ArgumentResolverException(String.format("Found invalid number value %s", value));
      }
    }

    private ApmString determineStringValue(String value) {
      Map<String, String> tokens = Optional.ofNullable(StringUtils.substringsBetween(value, "${", "}"))
          .map(Arrays::stream)
          .orElse(Stream.empty())
          .distinct()
          .collect(Collectors.toMap(Function.identity(), token -> variableHolder.get(token).getString()));
      StrSubstitutor strSubstitutor = new StrSubstitutor(tokens, "${", "}");
      return new ApmString(tokens.isEmpty() ? value : strSubstitutor.replace(value));
    }

    @Override
    public ApmType visitStringValue(ApmLangParser.StringValueContext ctx) {
      if (ctx.STRING_LITERAL() != null) {
        String value = Functions.toPlainString(ctx.STRING_LITERAL().toString());
        return determineStringValue(value);
      }
      throw new ArgumentResolverException(String.format("Found invalid string value %s", ctx));
    }

    @Override
    public ApmType visitPath(ApmLangParser.PathContext ctx) {
      String value = Functions.getPath(ctx);
      return determineStringValue(value);
    }

    @Override
    public ApmType visitVariable(ApmLangParser.VariableContext ctx) {
      String name = Functions.getIdentifier(ctx.variableIdentifier());
      return variableHolder.get(name);
    }
  }
}
