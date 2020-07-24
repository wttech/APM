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
package com.cognifide.apm.main.actions.deleteuser;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.main.actions.clearpermissions.Purge;
import com.cognifide.apm.main.actions.removeparents.RemoveParents;
import com.cognifide.apm.main.utils.MessagingUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public class DestroyUser implements Action {

  private final Action purge;

  private final Action remove;

  private final String userId;

  public DestroyUser(String userId) {
    this.purge = new Purge("/");
    this.userId = userId;
    remove = new RemoveUser(Collections.singletonList(userId));
  }

  @Override
  public ActionResult simulate(Context context) throws ActionExecutionException {
    ActionResult actionResult;
    try {
      User user = context.getAuthorizableManager().getUser(userId);
      context.setCurrentAuthorizable(user);
      Action removeFromGroups = new RemoveParents(getGroups(user));
      ActionResult purgeResult = purge.simulate(context);
      ActionResult removeFromGroupsResult = removeFromGroups.execute(context);
      ActionResult removeResult = remove.simulate(context);
      actionResult = purgeResult.merge(removeFromGroupsResult, removeResult);
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult = context.createActionResult();
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  @Override
  public ActionResult execute(Context context) throws ActionExecutionException {
    ActionResult actionResult;
    try {
      User user = context.getAuthorizableManager().getUser(userId);
      // local context is used here to not override current authorizable in given context
      Context localContext = context.newContext();
      localContext.setCurrentAuthorizable(user);
      Action removeFromGroups = new RemoveParents(getGroups(user));
      ActionResult purgeResult = purge.execute(localContext);
      ActionResult removeFromGroupsResult = removeFromGroups.execute(localContext);
      ActionResult removeResult = remove.execute(localContext);
      actionResult = purgeResult.merge(removeFromGroupsResult, removeResult);
      actionResult.setAuthorizable(context.getCurrentAuthorizable().getID() + " (ignored)");
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult = context.createActionResult();
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  private List<String> getGroups(User user) throws RepositoryException {
    List<String> groups = new ArrayList<>();
    Iterator<Group> groupIterator = user.declaredMemberOf();
    while (groupIterator.hasNext()) {
      Group group = groupIterator.next();
      groups.add(group.getID());
    }
    return groups;
  }
}
