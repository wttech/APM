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
package com.cognifide.cq.cqsm.foundation.actions.exclude;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Exclude implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(Exclude.class);

  private final List<String> authorizableIds;

  public Exclude(final List<String> authorizableIds) {
    this.authorizableIds = authorizableIds;
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
    ActionResult actionResult = new ActionResult();
    Group group = null;
    try {
      group = context.getCurrentGroup();
      actionResult.setAuthorizable(group.getID());
      LOGGER.info(String.format("Removing authorizables %s from group with id = %s",
          StringUtils.join(authorizableIds, ", "), group.getID()));
    } catch (ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
      return actionResult;
    } catch (RepositoryException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
      return actionResult;
    }

    List<String> errors = new ArrayList<>();
    for (String authorizableId : authorizableIds) {
      try {

        Authorizable authorizable = context.getAuthorizableManager().getAuthorizableIfExists(authorizableId);

        if (authorizable == null) {
          actionResult.logWarning(MessagingUtils.authorizableNotExists(authorizableId));
          continue;
        }

        if (execute) {
          group.removeMember(authorizable);
        }

        actionResult.logMessage(MessagingUtils.removedFromGroup(authorizableId, group.getID()));
      } catch (RepositoryException e) {
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
    return false;
  }

}
