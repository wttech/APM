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
package com.cognifide.apm.action.check.exists;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.cq.cqsm.core.actions.ActionUtils;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;

public class CheckAuthorizableExists implements Action {

  private final String path;

  private final String id;

  private final boolean shouldBeGroup;

  public CheckAuthorizableExists(final String id, final String path, final boolean shouldBeGroup) {
    this.id = id;
    this.shouldBeGroup = shouldBeGroup;
    this.path = path;
  }

  @Override
  public ActionResult simulate(Context context) {
    return process(context, false);
  }

  @Override
  public ActionResult execute(final Context context) {
    return process(context, true);
  }

  public ActionResult process(final Context context, boolean execute) {
    ActionResult actionResult = new ActionResult();
    try {
      Authorizable authorizable = null;
      if (shouldBeGroup) {
        authorizable = context.getAuthorizableManager().getGroupIfExists(id);
      } else {
        authorizable = context.getAuthorizableManager().getUserIfExists(id);
      }

      if (checkIfAuthIsNull(execute, actionResult, authorizable)) {
        return actionResult;
      }

      checkPath(actionResult, authorizable, execute);

    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  private boolean checkIfAuthIsNull(final boolean execute, final ActionResult actionResult,
      final Authorizable authorizable) throws RepositoryException {
    if (authorizable == null) {
      actionResult.logError("Authorizable " + id + " does not exist");
      if (execute) {
        actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
      }
      return true;
    }
    return false;
  }

  private void checkPath(final ActionResult actionResult, final Authorizable authorizable,
      final boolean execute) throws RepositoryException {
    if (path != null) {
      String realPath = authorizable.getPath();
      realPath = StringUtils.substringBeforeLast(realPath, "/");
      if (!StringUtils.equals(realPath, path)) {
        actionResult.logError("Authorizable " + id + " exists, but in different path: " + realPath);
        if (execute) {
          actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
        }
      }
    }
  }

  @Override
  public boolean isGeneric() {
    return true;
  }

}
