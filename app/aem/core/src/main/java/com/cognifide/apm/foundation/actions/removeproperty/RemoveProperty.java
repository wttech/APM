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
package com.cognifide.apm.foundation.actions.removeproperty;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.core.utils.MessagingUtils;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveProperty implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveProperty.class);

  private final String nameProperty;

  public RemoveProperty(final String nameProperty) {
    this.nameProperty = nameProperty;
  }

  @Override
  public ActionResult simulate(final Context context) {
    return process(context, true);
  }

  @Override
  public ActionResult execute(final Context context) {
    return process(context, false);
  }

  private ActionResult process(final Context context, boolean simulate) {
    ActionResult actionResult = context.createActionResult();

    try {
      Authorizable authorizable = context.getCurrentAuthorizable();
      actionResult.setAuthorizable(authorizable.getID());
      LOGGER.info(String.format("Removing property %s from authorizable with id = %s", nameProperty,
          authorizable.getID()));
      if (!simulate) {
        authorizable.removeProperty(nameProperty);
      }

      actionResult.logMessage("Property " + nameProperty + " for " + authorizable.getID() + " removed");
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
