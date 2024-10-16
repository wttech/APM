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
package com.cognifide.apm.checks.actions.password;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.checks.utils.ActionUtils;
import com.cognifide.apm.checks.utils.MessagingUtils;
import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.api.security.user.User;

public class CheckPassword implements Action {

  private final String userPassword;

  private final String userId;

  public CheckPassword(final String userId, final String userPassword) {
    this.userId = userId;
    this.userPassword = userPassword;
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
    try {
      if (!tryGetUser(context, actionResult)) {
        return actionResult;
      }
      boolean loginSuccessful = checkLogin(context.getSession().getRepository());
      if (!loginSuccessful) {
        actionResult.logError("Credentials for user " + userId + " seem invalid");
        if (execute) {
          actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
        }
      }
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  private boolean tryGetUser(final Context context, final ActionResult actionResult)
      throws ActionExecutionException, RepositoryException {
    User user = context.getAuthorizableManager().getUserIfExists(userId);
    if (user == null) {
      actionResult.logError(MessagingUtils.authorizableNotExists(userId));
      return false;
    }
    return true;
  }

  private boolean checkLogin(Repository repository) throws RepositoryException {
    boolean loginSuccessful = true;
    Credentials credentials = new SimpleCredentials(userId, userPassword.toCharArray());
    try {
      repository.login(credentials).logout();
    } catch (LoginException e) {
      loginSuccessful = false;
    }
    return loginSuccessful;
  }
}
