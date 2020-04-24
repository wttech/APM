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
package com.cognifide.cq.cqsm.foundation.actions.removeall;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.foundation.permissions.utils.JackrabbitAccessControlListUtil;
import java.security.Principal;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlManager;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveAll implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveAll.class);

  private final String path;

  public RemoveAll(final String path) {
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

  private ActionResult process(final Context context, boolean execute) {
    ActionResult actionResult = new ActionResult();
    try {
      Authorizable authorizable = context.getCurrentAuthorizable();
      actionResult.setAuthorizable(authorizable.getID());
      LOGGER.info(String.format("Removing all priveleges for authorizable with id = %s on path = %s",
          authorizable.getID(), path));
      if (execute) {
        removeAll(context, authorizable);
      }
      actionResult.logMessage("Removed all privileges for " + authorizable.getID() + " on " + path);
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  private void removeAll(final Context context, Authorizable authorizable) throws RepositoryException {
    final AccessControlManager accessControlManager = context.getAccessControlManager();
    final Principal principal = authorizable.getPrincipal();

    final JackrabbitAccessControlList jackrabbitAcl = JackrabbitAccessControlListUtil
        .getModifiableAcl(accessControlManager, path);
    final AccessControlEntry[] accessControlEntries = jackrabbitAcl.getAccessControlEntries();
    for (final AccessControlEntry accessControlEntry : accessControlEntries) {
      if (accessControlEntry.getPrincipal().equals(principal)) {
        jackrabbitAcl.removeAccessControlEntry(accessControlEntry);
      }
    }
    accessControlManager.setPolicy(path, jackrabbitAcl);
  }

  @Override
  public boolean isGeneric() {
    return false;
  }

}
