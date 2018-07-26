package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters;

public interface ActionInvoker {

  void runAction(Progress progress, String commandName, Parameters parameters);
}
