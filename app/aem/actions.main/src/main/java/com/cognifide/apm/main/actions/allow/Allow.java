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
package com.cognifide.apm.main.actions.allow;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.main.permissions.PermissionActionHelper;
import com.cognifide.apm.main.permissions.Restrictions;
import com.cognifide.apm.main.permissions.exceptions.PermissionException;
import com.cognifide.apm.main.utils.ActionUtils;
import com.cognifide.apm.main.utils.MessagingUtils;
import com.cognifide.apm.main.utils.PathUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Allow implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(Allow.class);

  private final String path;

  private final List<String> permissions;

  private final Restrictions restrictions;

  private final boolean ignoreNonExistingPaths;

  public Allow(String path, List<String> permissions,
      String glob, List<String> ntNames, List<String> itemNames, Map<String, Object> customRestrictions,
      boolean ignoreNonExistingPaths) {
    this.path = path;
    this.permissions = permissions;
    this.restrictions = new Restrictions(glob, ntNames, itemNames, customRestrictions);
    this.ignoreNonExistingPaths = ignoreNonExistingPaths;
  }

  @Override
  public ActionResult simulate(Context context) {
    return process(context, true);
  }

  @Override
  public ActionResult execute(Context context) {
    return process(context, false);
  }

  private ActionResult process(Context context, boolean simulate) {
    ActionResult actionResult = context.createActionResult();
    try {
      Authorizable authorizable = context.getCurrentAuthorizable();
      actionResult.setAuthorizable(authorizable.getID());
      if (context.isCompositeNodeStore() && PathUtils.isAppsOrLibsPath(path)) {
        actionResult.changeStatus(Status.SKIPPED, "Skipped adding allow privilege for " + authorizable.getID() + " on " + path);
      } else {
        context.getSession().getNode(path);
        PermissionActionHelper permissionActionHelper = new PermissionActionHelper(
            context.getValueFactory(), path, permissions, restrictions);
        LOGGER.info("Adding permissions {} for authorizable with id={} for path={} {}",
            permissions.toString(), context.getCurrentAuthorizable().getID(), path, restrictions);
        if (simulate) {
          permissionActionHelper.checkPermissions(context.getAccessControlManager());
        } else {
          permissionActionHelper.applyPermissions(context.getAccessControlManager(), authorizable.getPrincipal(), true);
        }
        actionResult.logMessage("Added allow privilege for " + authorizable.getID() + " on " + path);
        if (permissions.contains("MODIFY")) {
          String preparedGlob = recalculateGlob(restrictions.getGlob());
          new Allow(path, Collections.singletonList("MODIFY_PAGE"),
              preparedGlob + "*/jcr:content*", restrictions.getNtNames(), restrictions.getItemNames(),
              restrictions.getCustomRestrictions(),
              ignoreNonExistingPaths
          ).process(context, simulate);
        }
      }
    } catch (PathNotFoundException e) {
      if (ignoreNonExistingPaths) {
        actionResult.logWarning("Path " + path + " not found");
      } else {
        actionResult.logError("Path " + path + " not found");
      }
    } catch (RepositoryException | PermissionException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }

    if (actionResult.getStatus() == Status.ERROR) {
      actionResult.logError(ActionUtils.EXECUTION_INTERRUPTED_MSG);
    }
    return actionResult;
  }

  private String recalculateGlob(String glob) {
    String preparedGlob = "";
    if (!StringUtils.isBlank(glob)) {
      preparedGlob = glob;
      if (StringUtils.endsWith(glob, "*")) {
        preparedGlob = StringUtils.substring(glob, 0, StringUtils.lastIndexOf(glob, '*'));
      }
    }
    return preparedGlob;
  }
}
