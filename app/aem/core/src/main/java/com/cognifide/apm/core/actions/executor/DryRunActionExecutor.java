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
package com.cognifide.apm.core.actions.executor;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionException;
import com.cognifide.apm.core.actions.ActionDescriptor;
import com.cognifide.apm.core.actions.ActionFactory;
import com.cognifide.apm.core.actions.ActionResultImpl;

public final class DryRunActionExecutor extends AbstractActionExecutor {

  public DryRunActionExecutor(Context context, ActionFactory actionFactory) {
    super(context, actionFactory);
  }

  @Override
  public ActionResult execute(ActionDescriptor actionDescriptor) {
    try {
      final Action action = createAction(actionDescriptor);

      return action.simulate(context);
    } catch (ActionException e) {
      ActionResult actionResult = new ActionResultImpl();
      actionResult.logError(e.getMessage());

      return actionResult;
    }
  }

}
