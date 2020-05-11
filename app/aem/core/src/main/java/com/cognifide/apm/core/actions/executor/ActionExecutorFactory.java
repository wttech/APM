package com.cognifide.apm.core.actions.executor;

import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.core.actions.ActionFactory;

public final class ActionExecutorFactory {

  public static ActionExecutor create(ExecutionMode mode, Context context, ActionFactory actionFactory) {
    switch (mode) {
      case AUTOMATIC_RUN:
      case RUN:
        return new RunActionExecutor(context, actionFactory);
      case DRY_RUN:
        return new DryRunActionExecutor(context, actionFactory);
      default:
        return new ValidationActionExecutor(context, actionFactory);
    }
  }
}
