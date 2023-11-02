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
package com.cognifide.apm.main.actions.removechildren;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.exceptions.AuthorizableNotFoundException;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.main.utils.ActionUtils;
import com.cognifide.apm.main.utils.MessagingUtils;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveChildren implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveChildren.class);

  private final List<String> authorizableIds;

  public RemoveChildren(List<String> authorizableIds) {
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
    Group group;
    try {
      group = context.getCurrentGroup();
      actionResult.setAuthorizable(group.getID());
      LOGGER.info("Removing authorizables {} from group with id={}", String.join(", ", authorizableIds), group.getID());
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
      return actionResult;
    }

    for (String authorizableId : authorizableIds) {
      try {
        Authorizable authorizable = context.getAuthorizableManager().getAuthorizable(authorizableId);

        if (execute) {
          group.removeMember(authorizable);
        }

        actionResult.logMessage(MessagingUtils.removedFromGroup(authorizableId, group.getID()));
      } catch (RepositoryException | ActionExecutionException e) {
        actionResult.logError(MessagingUtils.createMessage(e));
      } catch (AuthorizableNotFoundException e) {
        actionResult.logWarning(MessagingUtils.createMessage(e));
      }
    }

    if (actionResult.getStatus() == Status.ERROR) {
      actionResult.logError(ActionUtils.EXECUTION_INTERRUPTED_MSG);
    }
    return actionResult;
  }
}
