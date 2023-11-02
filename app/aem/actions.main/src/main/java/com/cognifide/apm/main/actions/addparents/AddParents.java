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
package com.cognifide.apm.main.actions.addparents;

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

public class AddParents implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddParents.class);

  private final List<String> groupIds;

  private final boolean ignoreNonExistingGroups;

  public AddParents(List<String> groupIds, boolean ignoreNonExistingGroups) {
    this.groupIds = groupIds;
    this.ignoreNonExistingGroups = ignoreNonExistingGroups;
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
    Authorizable authorizable;
    try {
      authorizable = context.getCurrentAuthorizable();
      actionResult.setAuthorizable(authorizable.getID());
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
      return actionResult;
    }

    for (String id : groupIds) {
      try {
        Group group = context.getAuthorizableManager().getGroup(id);

        if (authorizable.isGroup()) {
          ActionUtils.checkCyclicRelations(group, (Group) authorizable);
        }
        LOGGER.info("Adding Authorizable with id={} to group with id={}", authorizable.getID(), group.getID());

        boolean flag = true;
        if (execute) {
          flag = group.addMember(authorizable);
          if (!flag) {
            flag = group.isMember(authorizable);
          }
        }
        if (flag) {
          actionResult.logMessage(MessagingUtils.addedToGroup(authorizable.getID(), id));
        } else {
          actionResult.logError(MessagingUtils.failedToAddToGroup(authorizable.getID(), id));
        }
      } catch (RepositoryException | ActionExecutionException e) {
        actionResult.logError(MessagingUtils.createMessage(e));
      } catch (AuthorizableNotFoundException e) {
        if (ignoreNonExistingGroups) {
          actionResult.logWarning(MessagingUtils.createMessage(e));
        } else {
          actionResult.logError(MessagingUtils.createMessage(e));
        }
      }
    }

    if (actionResult.getStatus() == Status.ERROR) {
      actionResult.logError(ActionUtils.EXECUTION_INTERRUPTED_MSG);
    }
    return actionResult;
  }
}
