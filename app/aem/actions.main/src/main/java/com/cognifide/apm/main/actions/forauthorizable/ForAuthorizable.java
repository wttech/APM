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
package com.cognifide.apm.main.actions.forauthorizable;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.exceptions.AuthorizableNotFoundException;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.main.utils.ActionUtils;
import com.cognifide.apm.main.utils.MessagingUtils;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public class ForAuthorizable implements Action {

  private final String id;

  private final boolean ignoreNonExistingAuthorizable;

  private final boolean shouldBeGroup;

  public ForAuthorizable(String id, boolean ignoreNonExistingAuthorizable, boolean shouldBeGroup) {
    this.id = id;
    this.ignoreNonExistingAuthorizable = ignoreNonExistingAuthorizable;
    this.shouldBeGroup = shouldBeGroup;
  }

  @Override
  public ActionResult simulate(Context context) {
    return process(context);
  }

  @Override
  public ActionResult execute(Context context) {
    return process(context);
  }

  public ActionResult process(Context context) {
    ActionResult actionResult = context.createActionResult();
    try {

      if (shouldBeGroup) {
        Group group = context.getAuthorizableManager().getGroup(id);
        context.setCurrentAuthorizable(group);
        actionResult.logMessage("Group with id: " + group.getID() + " set as current authorizable");
      } else {
        User user = context.getAuthorizableManager().getUser(id);
        context.setCurrentAuthorizable(user);
        actionResult.logMessage("User with id: " + user.getID() + " set as current authorizable");
      }

    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    } catch (AuthorizableNotFoundException e) {
      if (ignoreNonExistingAuthorizable) {
        actionResult.logWarning(MessagingUtils.createMessage(e));
      } else {
        actionResult.logError(MessagingUtils.createMessage(e));
      }
    }

    if (actionResult.getStatus() == Status.ERROR) {
      actionResult.logError(ActionUtils.EXECUTION_INTERRUPTED_MSG);
    }
    return actionResult;
  }
}
