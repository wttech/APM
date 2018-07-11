package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters;

public interface SingleActionFactory {

  String getName();

  Action create(Context context, Parameters parameters);
}
