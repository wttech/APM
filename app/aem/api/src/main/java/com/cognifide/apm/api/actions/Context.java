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
package com.cognifide.apm.api.actions;

import com.cognifide.apm.api.exceptions.ActionExecutionException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlManager;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public interface Context {

  ValueFactory getValueFactory() throws RepositoryException;

  Authorizable getCurrentAuthorizable() throws ActionExecutionException;

  Authorizable getCurrentAuthorizableIfExists();

  Group getCurrentGroup() throws ActionExecutionException;

  User getCurrentUser() throws ActionExecutionException;

  void clearCurrentAuthorizable();

  AccessControlManager getAccessControlManager();

  AuthorizableManager getAuthorizableManager();

  SessionSavingPolicy getSavingPolicy();

  JackrabbitSession getSession();

  void setCurrentAuthorizable(Authorizable currentAuthorizable);

  ActionResult createActionResult();

  Context newContext();
}
