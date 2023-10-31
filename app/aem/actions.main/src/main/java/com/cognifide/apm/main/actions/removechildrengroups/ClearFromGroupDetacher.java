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
package com.cognifide.apm.main.actions.removechildrengroups;

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.main.utils.MessagingUtils;
import java.util.Iterator;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearFromGroupDetacher {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClearFromGroupDetacher.class);

  private final Context context;

  private final boolean simulate;

  public ClearFromGroupDetacher(Context context, boolean simulate) {
    this.context = context;
    this.simulate = simulate;
  }

  public ActionResult detachMembersFromGroup() {
    ActionResult actionResult = context.createActionResult();

    try {
      Authorizable authorizable = context.getCurrentAuthorizable();

      if (authorizable.isGroup()) {
        final Group group = context.getCurrentGroup();
        LOGGER.info("Removing all members of group with id={}", group.getID());
        Iterator<Authorizable> groupMembers = getGroupMembers(actionResult, group);

        detachAllMembers(actionResult, group, groupMembers);
      } else {
        actionResult.logError("Child members can only be removed from groups");
      }
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  public ActionResult detachAuthorizableFromParents() {
    ActionResult actionResult = context.createActionResult();

    try {
      Authorizable currentAuthorizable = context.getCurrentAuthorizable();
      Iterator<Group> groups = getGroupParents(actionResult, currentAuthorizable);

      LOGGER.info("Removing all memberships of authorizable with id={}", currentAuthorizable.getID());
      detachFromParents(actionResult, currentAuthorizable, groups);
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  private void detachFromParents(final ActionResult actionResult, final Authorizable authorizable,
      Iterator<Group> groups) throws RepositoryException {
    while (groups.hasNext()) {
      Group currentGroup = groups.next();
      if (currentGroup.isGroup()) {
        if (!simulate) {
          currentGroup.removeMember(authorizable);
        }
        actionResult.logMessage(
            MessagingUtils.removedFromGroup(authorizable.getID(), currentGroup.getID()));
      }
    }
  }

  private Iterator<Group> getGroupParents(final ActionResult actionResult, final Authorizable authorizable)
      throws RepositoryException {
    String id = authorizable.getID();
    actionResult.setAuthorizable(id);
    Iterator<Group> groups = authorizable.memberOf();
    if (!groups.hasNext()) {
      actionResult.logWarning(MessagingUtils.groupIsMemberOfNoGroups(id));
    }
    return groups;
  }

  private void detachAllMembers(final ActionResult actionResult, final Group group,
      Iterator<Authorizable> groupMembers) throws RepositoryException {
    while (groupMembers.hasNext()) {
      Authorizable currentMember = groupMembers.next();
      if (currentMember.isGroup()) {
        if (!simulate) {
          group.removeMember(currentMember);
        }
        actionResult
            .logMessage(MessagingUtils.removedFromGroup(currentMember.getID(), group.getID()));
      }
    }
  }

  private Iterator<Authorizable> getGroupMembers(final ActionResult actionResult, final Group group)
      throws RepositoryException {
    String id = group.getID();
    actionResult.setAuthorizable(id);
    Iterator<Authorizable> groupMembers = group.getDeclaredMembers();
    if (!groupMembers.hasNext()) {
      actionResult.logWarning(MessagingUtils.groupHasNoMembers(id));
    }
    return groupMembers;
  }
}
