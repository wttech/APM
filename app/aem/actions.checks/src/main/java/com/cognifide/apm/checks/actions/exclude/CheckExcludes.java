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
package com.cognifide.apm.checks.actions.exclude;

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

public class CheckExcludes implements Action {

  private final List<String> authorizableIds;

  private final String groupId;

  public CheckExcludes(String groupId, List<String> authorizableIds) {
    this.groupId = groupId;
    this.authorizableIds = authorizableIds;
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
    Group group = tryGetGroup(context, actionResult);
    if (group == null) {
      return actionResult;
    }

    List<String> errors = new ArrayList<>();

    boolean checkFailed = checkMembers(context, actionResult, group, errors);

    if (execute && checkFailed) {
      actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
      return actionResult;
    }

    ActionUtils.logErrors(errors, actionResult);

    return actionResult;
  }

  private boolean checkMembers(Context context, ActionResult actionResult, Group group, List<String> errors) {
    boolean checkFailed = false;
    for (String authorizableId : authorizableIds) {
      try {
        Authorizable authorizable = context.getAuthorizableManager().getAuthorizableIfExists(authorizableId);
        if (authorizable == null) {
          actionResult.logWarning(MessagingUtils.authorizableNotExists(authorizableId));
          continue;
        }
        if (group.isMember(authorizable)) {
          actionResult.logError(authorizable.getID() + " belongs to group " + groupId);
          checkFailed = true;
        }
      } catch (RepositoryException e) {
        errors.add(MessagingUtils.createMessage(e));
      }
    }
    return checkFailed;
  }

  private Group tryGetGroup(Context context, ActionResult actionResult) {
    Group group = null;
    try {
      group = context.getAuthorizableManager().getGroup(groupId);
    } catch (RepositoryException | ActionExecutionException | AuthorizableNotFoundException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return group;
  }
}
