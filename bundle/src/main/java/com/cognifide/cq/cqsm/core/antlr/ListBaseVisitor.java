package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.apm.antlr.ApmLangBaseVisitor;
import java.util.ArrayList;
import java.util.List;

public class ListBaseVisitor<T> extends ApmLangBaseVisitor<List<T>> {

  @Override
  protected List<T> defaultResult() {
    return new ArrayList<>();
  }

  @Override
  protected List<T> aggregateResult(List<T> aggregate, List<T> nextResult) {
    if (nextResult != null) {
      aggregate.addAll(nextResult);
    }
    return aggregate;
  }
}
