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
package com.cognifide.apm.main.actions.deleteuser;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.main.utils.MessagingUtils;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveUser implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveUser.class);

  private final List<String> ids;

  public RemoveUser(final List<String> ids) {
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
    LOGGER.info(String.format("Removing users with ids = %s", StringUtils.join(ids, ", ")));
    for (String id : ids) {
      try {
        User user = context.getAuthorizableManager().getUserIfExists(id);
        if (user != null) {
          context.getAuthorizableManager().markAuthorizableAsRemoved(user);
          if (execute) {
            context.getAuthorizableManager().removeUser(user);
          }
          actionResult.logMessage("User with id: " + id + " removed");
        }
      } catch (RepositoryException | ActionExecutionException e) {
        errors.add(MessagingUtils.createMessage(e));
      }
    }

    if (!errors.isEmpty()) {
      for (String error : errors) {
        actionResult.logError(error);
      }
      actionResult.logError("Execution interrupted");
    }
    return actionResult;
  }

  @Override
  public boolean isGeneric() {
    return true;
  }
}
