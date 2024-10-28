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
package com.cognifide.apm.checks.actions.notexists;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.checks.utils.ActionUtils;
import com.cognifide.apm.checks.utils.MessagingUtils;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;

public class CheckNotExists implements Action {

  private final List<String> ids;

  public CheckNotExists(final List<String> ids) {
    this.ids = ids;
  }

  @Override
  public ActionResult simulate(Context context) {
    return process(context, false);
  }

  @Override
  public ActionResult execute(final Context context) {
    return process(context, true);
  }

  private ActionResult process(final Context context, boolean execute) {
    ActionResult actionResult = context.createActionResult();
    List<String> errors = new ArrayList<>();

    boolean checkFailed = checkAuthorizables(context, actionResult, errors);

    if (execute && checkFailed) {
      actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
      return actionResult;
    }

    logErrors(actionResult, errors);

    return actionResult;
  }

  private boolean checkAuthorizables(final Context context, ActionResult actionResult,
      List<String> errors) {
    boolean checkFailed = false;
    for (String authorizableId : ids) {
      try {
        Authorizable authorizable = context.getAuthorizableManager().getAuthorizableIfExists(authorizableId);
        if (authorizable != null) {
          actionResult.logError("Authorizable " + authorizableId + " exists");
          checkFailed = true;
        }
      } catch (final RepositoryException e) {
        errors.add(MessagingUtils.createMessage(e));
      }
    }
    return checkFailed;
  }

  private void logErrors(ActionResult actionResult, List<String> errors) {
    for (String error : errors) {
      actionResult.logError(error);
    }
  }
}
