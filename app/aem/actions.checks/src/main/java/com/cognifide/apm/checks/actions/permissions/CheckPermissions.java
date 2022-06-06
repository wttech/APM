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
package com.cognifide.apm.checks.actions.permissions;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.checks.utils.ActionUtils;
import com.cognifide.apm.checks.utils.MessagingUtils;
import com.day.cq.security.util.CqActions;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;

public class CheckPermissions implements Action {

  private final String path;

  private final String glob;

  private final List<String> permissions;

  private final boolean isAllow;

  private final String authorizableId;

  public CheckPermissions(final String authorizableId, final String path, final String glob,
      final List<String> permissions, boolean isAllow) {
    this.authorizableId = authorizableId;
    this.path = path;
    this.glob = glob;
    this.permissions = permissions;
    this.isAllow = isAllow;
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
      final Authorizable authorizable = context.getAuthorizableManager().getAuthorizable(authorizableId);

      final Set<Principal> authorizablesToCheck = getAuthorizablesToCheck(authorizable, context);

      final CqActions actions = new CqActions(context.getSession());

      final List<String> privilegesToCheck = preparePrivilegesToCheck();

      if (StringUtils.isBlank(glob)) {
        if (checkPermissionsForPath(authorizablesToCheck, actions, privilegesToCheck, path)) {
          logFailure(execute, actionResult, authorizable, path);
        } else {
          actionResult.logMessage(
              "All required privileges are set for " + authorizable.getID() + " on " + path);
        }
      } else {
        checkPermissionsForGlob(context.getSession(), execute, actionResult, authorizable, authorizablesToCheck,
            actions,
            privilegesToCheck);
      }

    } catch (final PathNotFoundException e) {
      actionResult.logError("Path " + path + " not found");
    } catch (RepositoryException | ActionExecutionException | LoginException e) {
      actionResult.logError(MessagingUtils.createMessage(e));
    }
    return actionResult;
  }

  private void checkPermissionsForGlob(Session session, final boolean execute, final ActionResult actionResult,
      final Authorizable authorizable, final Set<Principal> authorizablesToCheck,
      final CqActions actions, final List<String> privilegesToCheck)
      throws RepositoryException, LoginException {
    final List<String> subpaths = getAllSubpaths(session, path);
    Pattern pattern = Pattern.compile(path + StringUtils.replace(glob, "*", ".*"));
    boolean foundMatch = false;
    boolean failed = false;
    for (String subpath : subpaths) {
      if (pattern.matcher(subpath).matches()) {
        foundMatch = true;
        failed = checkPermissionsForPath(authorizablesToCheck, actions, privilegesToCheck, subpath);
        if (failed) {
          logFailure(execute, actionResult, authorizable, subpath);
          break;
        }
      }
    }
    if (!foundMatch) {
      actionResult
          .logError("No match was found for " + authorizable.getID() + " for given glob " + glob);
      if (execute) {
        actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
      }
    } else if (!failed) {
      actionResult.logMessage(
          "All required privileges are set for " + authorizable.getID() + " on " + path);
    }
  }

  private boolean checkPermissionsForPath(final Set<Principal> authorizablesToCheck,
      final CqActions actions, final List<String> privilegesToCheck, String subpath)
      throws RepositoryException {
    Collection<String> allowedActions = actions.getAllowedActions(subpath, authorizablesToCheck);
    final boolean containsAll = allowedActions.containsAll(privilegesToCheck);
    return (!containsAll && isAllow) || (containsAll && !isAllow);
  }

  private void logFailure(boolean execute, ActionResult actionResult, final Authorizable authorizable,
      String subpath) throws RepositoryException {
    actionResult.logError(
        "Not all required privileges are set for " + authorizable.getID() + " on " + subpath);
    if (execute) {
      actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
    }
  }

  private List<String> getAllSubpaths(Session session, final String path) throws RepositoryException, LoginException {
    List<String> subPaths = new ArrayList<>();
    Node node = session.getNode(path);
    subPaths.addAll(crawl(node));

    return subPaths;
  }

  private List<String> crawl(final Node node) throws RepositoryException {
    List<String> paths = new ArrayList<>();
    paths.add(node.getPath());
    for (NodeIterator iter = node.getNodes(); iter.hasNext(); ) {
      paths.addAll(crawl(iter.nextNode()));
    }
    return paths;
  }

  private Set<Principal> getAuthorizablesToCheck(Authorizable authorizable, Context context)
      throws RepositoryException {
    Set<Principal> principals = new HashSet<>();
    Principal principal = authorizable.getPrincipal();
    principals.add(principal);

    for (PrincipalIterator it = (context.getSession()).getPrincipalManager()
        .getGroupMembership(principal); it.hasNext(); ) {
      principals.add(it.nextPrincipal());
    }

    return principals;
  }

  private List<String> preparePrivilegesToCheck() throws RepositoryException {
    return Lists.transform(permissions, new toLowerCase());
  }

  private static class toLowerCase implements Function<String, String> {

    @Override
    public String apply(String input) {
      return input.toLowerCase();
    }
  }
}
