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
package com.cognifide.apm.main.actions.setpassword;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.main.utils.MessagingUtils;
import javax.jcr.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.api.security.user.User;

@Slf4j
public class SetPassword implements Action {

  private final String password;

  public SetPassword(final String password) {
    this.password = password;
  }

  @Override
  public ActionResult simulate(final Context context) {
    return process(context, false);
  }

  @Override
  public ActionResult execute(final Context context) {
    return process(context, true);
  }

  private ActionResult process(final Context context, boolean execute) {
    ActionResult actionResult = context.createActionResult();
    try {
      User user = context.getCurrentUser();
      actionResult.setAuthorizable(user.getID());
      log.info("Setting password for user with id = {}", user.getID());
      if (execute) {
        user.changePassword(password);
      }
      actionResult.logMessage(MessagingUtils.newPasswordSet(user.getID()));
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }
}
