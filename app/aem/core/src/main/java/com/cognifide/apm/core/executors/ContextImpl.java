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
package com.cognifide.apm.core.executors;

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.AuthorizableManager;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.actions.SessionSavingPolicy;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.apm.core.actions.ActionResultImpl;
import com.cognifide.apm.core.sessions.SessionSavingPolicyImpl;
import com.cognifide.apm.core.utils.AuthorizableManagerImpl;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public final class ContextImpl implements Context {

  @Getter
  private final AccessControlManager accessControlManager;

  @Getter
  private final AuthorizableManager authorizableManager;

  @Getter
  private final SessionSavingPolicy savingPolicy;

  @Getter
  private final JackrabbitSession session;

  @Setter
  private Authorizable currentAuthorizable;

  @Getter
  private boolean compositeNodeStore;

  public ContextImpl(JackrabbitSession session, boolean compositeNodeStore) throws RepositoryException {
    this.session = session;
    this.accessControlManager = session.getAccessControlManager();
    this.authorizableManager = new AuthorizableManagerImpl(session.getUserManager());
    this.savingPolicy = new SessionSavingPolicyImpl();
    this.compositeNodeStore = compositeNodeStore;
  }

  private ContextImpl(AccessControlManager accessControlManager,
                      AuthorizableManager authorizableManager, SessionSavingPolicy savingPolicy,
                      JackrabbitSession session, boolean compositeNodeStore) {
    this.accessControlManager = accessControlManager;
    this.authorizableManager = authorizableManager;
    this.savingPolicy = savingPolicy;
    this.session = session;
    this.compositeNodeStore = compositeNodeStore;
  }

  @Override
  public ValueFactory getValueFactory() throws RepositoryException {
    return session.getValueFactory();
  }

  @Override
  public Authorizable getCurrentAuthorizable() throws ActionExecutionException {
    if (currentAuthorizable == null) {
      throw new ActionExecutionException("Current authorizable not set.");
    }
    return currentAuthorizable;
  }

  @Override
  public Authorizable getCurrentAuthorizableIfExists() {
    return currentAuthorizable;
  }

  @Override
  public Group getCurrentGroup() throws ActionExecutionException {
    if (getCurrentAuthorizable() instanceof User) {
      throw new ActionExecutionException("Current authorizable is not a group");
    }
    return (Group) currentAuthorizable;
  }

  @Override
  public User getCurrentUser() throws ActionExecutionException {
    if (getCurrentAuthorizable() instanceof Group) {
      throw new ActionExecutionException("Current authorizable is not a user");
    }
    return (User) currentAuthorizable;
  }

  @Override
  public void clearCurrentAuthorizable() {
    this.currentAuthorizable = null;
  }

  @Override
  public ActionResult createActionResult() {
    return new ActionResultImpl();
  }

  @Override
  public Context newContext() {
    return new ContextImpl(accessControlManager, authorizableManager, savingPolicy, session, compositeNodeStore);
  }

}