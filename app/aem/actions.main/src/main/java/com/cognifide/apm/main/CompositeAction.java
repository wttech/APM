package com.cognifide.apm.main;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;

public class CompositeAction implements Action {

  private final List<Action> actions;
  private final boolean generic;

  public CompositeAction(boolean generic, List<Action> actions) {
    this.generic = generic;
    this.actions = ImmutableList.copyOf(actions);
  }

  public CompositeAction(boolean generic, Action... actions) {
    this.generic = generic;
    this.actions = ImmutableList.copyOf(actions);
  }

  @Override
  public ActionResult simulate(Context context) throws ActionExecutionException {
    ActionResult result = context.createActionResult();
    List<ActionResult> actionResults = Lists.newArrayListWithCapacity(actions.size());
    for (Action action : actions) {
      actionResults.add(action.simulate(context));
    }
    return result.merge(actionResults);
  }

  @Override
  public ActionResult execute(Context context) throws ActionExecutionException {
    ActionResult result = context.createActionResult();
    List<ActionResult> actionResults = Lists.newArrayListWithCapacity(actions.size());
    for (Action action : actions) {
      actionResults.add(action.execute(context));
    }
    return result.merge(actionResults);
  }

  @Override
  public boolean isGeneric() {
    return generic;
  }
}
