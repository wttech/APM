/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.cq.cqsm.foundation.actions.createauthorizablethen;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.logger.Status;
import com.cognifide.cq.cqsm.foundation.actions.createauthorizable.CreateAuthorizable;
import com.cognifide.cq.cqsm.foundation.actions.createauthorizable.CreateAuthorizableStrategy;
import com.cognifide.cq.cqsm.foundation.actions.forauthorizable.ForAuthorizable;

public class CreateAuthorizableThen implements Action {

  private final CreateAuthorizable createAuthorizable;

  private final ForAuthorizable forAuthorizable;

  public CreateAuthorizableThen(final String id, final String password, final String path,
      final Boolean ignoreIfExists, final CreateAuthorizableStrategy createStrategy,
      final Boolean shouldBeGroup) {
    this.createAuthorizable = new CreateAuthorizable(id, password, path, ignoreIfExists, createStrategy);
    this.forAuthorizable = new ForAuthorizable(id, shouldBeGroup);
  }

  @Override
  public ActionResult simulate(final Context context) {
    return process(context, true);
  }

  @Override
  public ActionResult execute(final Context context) {
    return process(context, false);
  }

  public ActionResult process(final Context context, boolean simulate) {
    ActionResult actionResult = createAuthorizable.process(context, simulate);
    if (actionResult.getStatus() != Status.ERROR) {
      ActionResult createActionResult = actionResult;
      actionResult = forAuthorizable.process(context);
      actionResult.getMessages().addAll(0, createActionResult.getMessages());
    }
    return actionResult;
  }

  @Override
  public boolean isGeneric() {
    return true;
  }

}
