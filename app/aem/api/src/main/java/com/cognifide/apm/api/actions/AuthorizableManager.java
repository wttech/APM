/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import java.security.Principal;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public interface AuthorizableManager {

  Authorizable getAuthorizableIfExists(String id) throws RepositoryException;

  Authorizable getAuthorizable(String id) throws ActionExecutionException, RepositoryException;

  void markAuthorizableAsRemoved(Authorizable authorizable) throws RepositoryException;

  Group getGroupIfExists(String id) throws RepositoryException, ActionExecutionException;

  Group getGroup(String id) throws ActionExecutionException, RepositoryException;

  Group createGroup(String id, Principal namePrincipal, String path) throws RepositoryException;

  Group createMockGroup(String id);

  void removeGroup(Group group) throws RepositoryException;

  User getUserIfExists(String id) throws ActionExecutionException, RepositoryException;

  User getUser(String id) throws ActionExecutionException, RepositoryException;

  User createUser(String id, String password, Principal namePrincipal, String path) throws RepositoryException;

  User createSystemUser(String id, String path) throws RepositoryException;

  User createMockUser(String id);

  void removeUser(User user) throws RepositoryException;

  Principal createMockPrincipal(String name);
}
