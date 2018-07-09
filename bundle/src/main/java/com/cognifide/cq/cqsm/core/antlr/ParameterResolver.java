package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.apm.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.antlr.ApmLangParser;
import com.cognifide.apm.antlr.ApmLangParser.ParameterContext;

public class ParameterResolver {

  private final VariableHolder variableHolder;
  private final Resolver resolver;

  public ParameterResolver(VariableHolder variableHolder) {
    this.variableHolder = variableHolder;
    this.resolver = new Resolver();
  }

  public String resolve(ParameterContext context) {
    return resolver.visit(context);
  }

  private class Resolver extends ApmLangBaseVisitor<String> {

    @Override
    protected String defaultResult() {
      return "";
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
      if (isNotEmpty(aggregate) && isNotEmpty(nextResult)) {
        return String.format("%s%n%s", aggregate, nextResult);
      } else if (isNotEmpty(aggregate)) {
        return aggregate;
      } else if (isNotEmpty(nextResult)) {
        return nextResult;
      }
      return "";
    }

    private boolean isNotEmpty(String nextResult) {
      return nextResult != null && !nextResult.isEmpty();
    }

    @Override
    public String visitParameter(ApmLangParser.ParameterContext ctx) {
      if (ctx.STRING_LITERAL() != null) {
        return ctx.STRING_LITERAL().toString();
      } else if (ctx.IDENTIFIER() != null) {
        return ctx.IDENTIFIER().toString();
      } else {
        return visit(ctx.variable());
      }
    }

    @Override
    public String visitVariable(ApmLangParser.VariableContext ctx) {
      return variableHolder.get(ctx.IDENTIFIER().toString());
    }
  }
}
