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
package com.cognifide.apm.checks.actions.include;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.exceptions.AuthorizableNotFoundException;
import com.cognifide.apm.checks.utils.ActionUtils;
import com.cognifide.apm.checks.utils.MessagingUtils;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;

public class CheckIncludes implements Action {

  private final List<String> groupIds;

  private final String authorizableId;

  private final boolean ifExists;

  public CheckIncludes(String id, List<String> groupIds, boolean ifExists) {
    this.authorizableId = id;
    this.groupIds = groupIds;
    this.ifExists = ifExists;
  }

  @Override
  public ActionResult simulate(Context context) {
    return process(context, false);
  }

  @Override
  public ActionResult execute(Context context) {
    return process(context, true);
  }

  private ActionResult process(Context context, boolean execute) {
    ActionResult actionResult = context.createActionResult();
    Group authorizable = tryGetGroup(context, actionResult);
    if (authorizable == null) {
      return actionResult;
    }

    List<String> errors = new ArrayList<>();

    boolean checkFailed = checkMembers(context, actionResult, authorizable, errors, ifExists);

    if (execute && checkFailed) {
      actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
      return actionResult;
    }

    ActionUtils.logErrors(errors, actionResult);

    return actionResult;
  }

  private boolean checkMembers(Context context, ActionResult actionResult, Group authorizable, List<String> errors, boolean ifExists) {
    boolean checkFailed = false;
    for (String id : groupIds) {
      try {
        Authorizable group = context.getAuthorizableManager().getAuthorizable(id);
        if (!authorizable.isMember(group)) {
          actionResult.logError(id + " is excluded from group " + authorizableId);
          checkFailed = true;
        } else {
          actionResult.logMessage(id + " is a member of group " + authorizableId);
        }
      } catch (AuthorizableNotFoundException e) {
        if (ifExists) {
          actionResult.logWarning(MessagingUtils.authorizableNotExists(id));
        } else {
          errors.add(MessagingUtils.createMessage(e));
        }
      } catch (RepositoryException | ActionExecutionException e) {
        errors.add(MessagingUtils.createMessage(e));
      }
    }
    return checkFailed;
  }

  private Group tryGetGroup(Context context, ActionResult actionResult) {
    try {
      return context.getAuthorizableManager().getGroup(authorizableId);
    } catch (RepositoryException | ActionExecutionException | AuthorizableNotFoundException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return null;
  }
}
