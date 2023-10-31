/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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

  public CompositeAction(List<Action> actions) {
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
}
