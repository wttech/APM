package com.cognifide.cq.cqsm

import com.cognifide.apm.antlr.ApmLangBaseVisitor

class ListReturningVisitor<T> extends ApmLangBaseVisitor<List<T>> {

    @Override
    protected List<T> defaultResult() {
        return []
    }

    @Override
    protected List<T> aggregateResult(List<T> aggregate, List<T> nextResult) {
        if (nextResult != null) {
            aggregate.addAll(nextResult)
        }
        return aggregate
    }
}
