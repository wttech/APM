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
package com.cognifide.apm.main.actions.deletegroup;

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
import org.apache.jackrabbit.api.security.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteGroup implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteGroup.class);

  private final List<String> ids;

  public DeleteGroup(List<String> ids) {
    this.ids = ids;
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

    LOGGER.info("Removing groups with ids={}", String.join(", ", ids));
    for (String id : ids) {
      try {
        Group group = context.getAuthorizableManager().getGroup(id);
        context.getAuthorizableManager().markAuthorizableAsRemoved(group);
        if (execute) {
          context.getAuthorizableManager().removeGroup(group);
        }
        actionResult.logMessage("Group with id: " + id + " removed");
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
