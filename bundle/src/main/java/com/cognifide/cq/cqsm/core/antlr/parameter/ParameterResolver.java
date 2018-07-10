package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.apm.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.antlr.ApmLangParser;
import com.cognifide.apm.antlr.ApmLangParser.ParameterContext;
import com.cognifide.apm.antlr.ApmLangParser.StringConstContext;
import com.cognifide.apm.antlr.ApmLangParser.StringValueContext;
import com.cognifide.cq.cqsm.core.antlr.type.ApmNull;
import com.cognifide.cq.cqsm.core.antlr.type.ApmString;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;

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
    public ApmType visitStringValue(StringValueContext ctx) {
      return new ApmString(ctx.STRING_LITERAL().toString());
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
