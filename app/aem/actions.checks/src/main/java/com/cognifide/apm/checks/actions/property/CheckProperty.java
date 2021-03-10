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
package com.cognifide.apm.checks.actions.property;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.checks.utils.ActionUtils;
import com.cognifide.apm.checks.utils.MessagingUtils;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;

public class CheckProperty implements Action {

  private final String propertyName;

  private final String propertyValue;

  private final String authorizableId;

  public CheckProperty(final String authorizableId, final String name, final String value) {
    this.authorizableId = authorizableId;
    this.propertyName = name;
    this.propertyValue = value;
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
      Authorizable authorizable = context.getAuthorizableManager().getAuthorizable(authorizableId);

      if (!checkIfAuthHasProperty(execute, actionResult, authorizable)) {
        return actionResult;
      }

      if (checkPropertyExists(authorizable)) {
        return actionResult;
      }

      actionResult.logError(
          "Authorizable " + authorizableId + ": unexpected value of property: " + propertyName);
      if (execute) {
        actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
      }
    } catch (final RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  private boolean checkPropertyExists(final Authorizable authorizable) throws RepositoryException {
    Value[] values = authorizable.getProperty(propertyName);
    for (Value val : values) {
      if ((val.getType() == PropertyType.STRING) && StringUtils
          .equals(val.getString(), propertyValue)) {
        return true;
      }
    }
    return false;
  }

  private boolean checkIfAuthHasProperty(boolean execute, ActionResult actionResult,
      Authorizable authorizable) throws RepositoryException {
    if (!authorizable.hasProperty(propertyName)) {
      actionResult.logError("Authorizable " + authorizableId + ": no such property: " + propertyName);
      if (execute) {
        actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
      }
      return false;
    }
    return true;
  }
}
