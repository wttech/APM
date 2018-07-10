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
import com.cognifide.cq.cqsm.core.antlr.VariableHolder;
import com.cognifide.cq.cqsm.core.antlr.type.ApmBoolean;
import com.cognifide.cq.cqsm.core.antlr.type.ApmList;
import com.cognifide.cq.cqsm.core.antlr.type.ApmNull;
import com.cognifide.cq.cqsm.core.antlr.type.ApmNumber;
import com.cognifide.cq.cqsm.core.antlr.type.ApmString;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import com.cognifide.cq.cqsm.core.antlr.type.ApmValue;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;

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
      List<ApmValue> values = new ArrayList<>();
      int n = ctx.getChildCount();
      for (int i = 0; i < n; i++) {
        ParseTree c = ctx.getChild(i);
        ApmType result = c.accept(this);
        if (result instanceof ApmValue) {
          values.add((ApmValue) result);
        }
      }
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
      String stringLiteral = ctx.STRING_LITERAL().toString();
      return new ApmString(stringLiteral.substring(1, stringLiteral.length() - 1));
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
