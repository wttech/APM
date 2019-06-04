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
package com.cognifide.cq.cqsm.foundation.actions.allow;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.foundation.permissions.PermissionActionHelper;
import com.cognifide.cq.cqsm.foundation.permissions.Restrictions;
import com.cognifide.cq.cqsm.foundation.permissions.exceptions.PermissionException;
import java.util.Collections;
import java.util.List;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
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
      String glob, List<String> ntNames,
      List<String> itemNames, boolean ignoreNonExistingPaths) {
    this.path = path;
    this.permissions = permissions;
    this.restrictions = new Restrictions(glob, ntNames, itemNames);
    this.ignoreNonExistingPaths = ignoreNonExistingPaths;
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
    ActionResult actionResult = new ActionResult();
    try {
      Authorizable authorizable = context.getCurrentAuthorizable();
      actionResult.setAuthorizable(authorizable.getID());
      context.getSession().getNode(path);
      final PermissionActionHelper permissionActionHelper = new PermissionActionHelper(
          context.getValueFactory(), path, permissions, restrictions);
      LOGGER.info(String.format("Adding permissions %s for authorizable with id = %s for path = %s %s",
          permissions.toString(), context.getCurrentAuthorizable().getID(), path, restrictions));
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
            ignoreNonExistingPaths
        ).process(context, simulate);
      }
    } catch (final PathNotFoundException e) {
      if (ignoreNonExistingPaths) {
        actionResult.logWarning("Path " + path + " not found");
      } else {
        actionResult.logError("Path " + path + " not found");
        return actionResult;
      }
    } catch (RepositoryException | PermissionException | ActionExecutionException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
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

  @Override
  public boolean isGeneric() {
    return false;
  }

}
