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
package com.cognifide.cq.cqsm.foundation.actions.setpassword;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetPassword implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetPassword.class);

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
    ActionResult actionResult = new ActionResult();
    try {
      User user = context.getCurrentUser();
      actionResult.setAuthorizable(user.getID());
      LOGGER.info(String.format("Setting password for user with id = %s", user.getID()));
      if (execute) {
        user.changePassword(password);
      }
      actionResult.logMessage(MessagingUtils.newPasswordSet(user.getID()));
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  @Override
  public boolean isGeneric() {
    return false;
  }
}
