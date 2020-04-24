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
package com.cognifide.cq.cqsm.foundation.actions.removegroup;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveGroup implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveGroup.class);

  private final List<String> ids;

  public RemoveGroup(final List<String> ids) {
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

    List<String> errors = new ArrayList<>();
    ActionResult actionResult = new ActionResult();
    LOGGER.info(String.format("Removing groups with ids = %s", StringUtils.join(ids, ", ")));
    for (String id : ids) {
      try {
        Group group = context.getAuthorizableManager().getGroupIfExists(id);
        if (group != null) {
          context.getAuthorizableManager().markAuthorizableAsRemoved(group);
          if (execute) {
            context.getAuthorizableManager().removeGroup(group);
          }
          actionResult.logMessage("Group with id: " + id + " removed");
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
