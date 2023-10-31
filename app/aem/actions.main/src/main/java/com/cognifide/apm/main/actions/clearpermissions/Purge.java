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
import com.cognifide.apm.api.actions.Message;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.main.utils.MessagingUtils;
import com.cognifide.apm.main.utils.PathUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlPolicy;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.oak.api.Type;
import org.apache.jackrabbit.oak.spi.security.authorization.accesscontrol.ACE;
import org.apache.jackrabbit.oak.spi.security.authorization.accesscontrol.AbstractAccessControlList;
import org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionConstants;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.Restriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Purge implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(Purge.class);

  private static final String PERMISSION_STORE_PATH = "/jcr:system/rep:permissionStore/crx.default/";

  private final String path;

  public Purge(String path) {
    this.path = path;
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
    try {
      Authorizable authorizable = context.getCurrentAuthorizable();
      actionResult.setAuthorizable(authorizable.getID());
      if (context.isCompositeNodeStore() && PathUtils.isAppsOrLibsPath(path)) {
        actionResult.changeStatus(Status.SKIPPED, "Skipped purging privileges for " + authorizable.getID() + " on " + path);
      } else {
        LOGGER.info("Purging privileges for authorizable with id={} under path={}", authorizable.getID(), path);
        if (execute) {
          purge(context, actionResult);
        }
        actionResult.logMessage("Purged privileges for " + authorizable.getID() + " on " + path);
      }
    } catch (RepositoryException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }

    return actionResult;
  }

  private void purge(Context context, ActionResult actionResult)
      throws RepositoryException, ActionExecutionException {
    Set<String> accessControlledPaths = getAccessControlledPaths(context);
    String normalizedPath = normalizePath(path);
    for (String parentPath : accessControlledPaths) {
      String normalizedParentPath = normalizePath(parentPath);
      boolean isUsersPermission = parentPath.startsWith(context.getCurrentAuthorizable().getPath());
      if (StringUtils.startsWith(normalizedParentPath, normalizedPath) && !isUsersPermission) {
        RemoveAll removeAll = new RemoveAll(parentPath);
        ActionResult removeAllResult = removeAll.execute(context);
        if (Status.ERROR.equals(removeAllResult.getStatus())) {
          copyErrorMessages(removeAllResult, actionResult);
        }
      }
    }
  }

  private void copyErrorMessages(ActionResult from, ActionResult to) {
    for (Message msg : from.getMessages()) {
      if (Message.ERROR.equals(msg.getType())) {
        to.logWarning(msg.getText() + ", continuing");
      }
    }
  }

  private Set<String> getAccessControlledPaths(Context context)
      throws ActionExecutionException, RepositoryException {
    Set<String> result = new HashSet<>();
    JackrabbitSession session = context.getSession();
    String path = PERMISSION_STORE_PATH + context.getCurrentAuthorizable().getID();
    if (session.nodeExists(path)) {
      Node node = session.getNode(path);
      NodeIterator nodes = node.getNodes();
      while (nodes.hasNext()) {
        node = nodes.nextNode();
        if (node.hasProperty(PermissionConstants.REP_ACCESS_CONTROLLED_PATH)) {
          result.add(node.getProperty(PermissionConstants.REP_ACCESS_CONTROLLED_PATH).getString());
        }
      }
    } else {
      JackrabbitAccessControlManager accessControlManager = (JackrabbitAccessControlManager) session.getAccessControlManager();
      AccessControlPolicy[] accessControlPolicies = accessControlManager.getPolicies(context.getCurrentAuthorizable().getPrincipal());
      for (AccessControlPolicy accessControlPolicy : accessControlPolicies) {
        AbstractAccessControlList abstractAccessControlList = (AbstractAccessControlList) accessControlPolicy;
        List<? extends JackrabbitAccessControlEntry> jackrabbitAccessControlEntries = abstractAccessControlList.getEntries();
        for (JackrabbitAccessControlEntry jackrabbitAccessControlEntry : jackrabbitAccessControlEntries) {
          Set<Restriction> restrictions = ((ACE) jackrabbitAccessControlEntry).getRestrictions();
          for (Restriction restriction : restrictions) {
            if (Type.PATH.equals(restriction.getProperty().getType())) {
              result.add(restriction.getProperty().getValue(Type.PATH));
            }
          }
        }
      }
    }
    return result;
  }

  private String normalizePath(String path) {
    return path + (path.endsWith("/") ? "" : "/");
  }
}
