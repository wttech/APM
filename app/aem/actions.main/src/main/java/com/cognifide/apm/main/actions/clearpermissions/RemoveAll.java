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
package com.cognifide.apm.main.actions.clearpermissions;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.main.permissions.utils.JackrabbitAccessControlListUtil;
import com.cognifide.apm.main.utils.MessagingUtils;
import java.security.Principal;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;

@Slf4j
public class RemoveAll implements Action {

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
    ActionResult actionResult = context.createActionResult();
    try {
      Authorizable authorizable = context.getCurrentAuthorizable();
      actionResult.setAuthorizable(authorizable.getID());
      log.info("Removing all priveleges for authorizable with id = {} on path = {}",
          authorizable.getID(), path);
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
}
