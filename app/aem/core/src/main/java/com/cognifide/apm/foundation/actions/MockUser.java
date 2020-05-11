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
package com.cognifide.apm.foundation.actions;

import java.security.Principal;
import java.util.Iterator;
import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Impersonation;
import org.apache.jackrabbit.api.security.user.User;

public class MockUser implements User {

  private final String id;

  public MockUser(String id) {
    this.id = id;
  }

  @Override
  public String getID() throws RepositoryException {
    return id;
  }

  @Override
  public boolean isGroup() {
    return false;
  }

  @Override
  public Principal getPrincipal() throws RepositoryException {
    return new MockPrincipal(id);
  }

  @Override
  public Iterator<Group> declaredMemberOf() throws RepositoryException {
    return null;
  }

  @Override
  public Iterator<Group> memberOf() throws RepositoryException {
    return null;
  }

  @Override
  public void remove() throws RepositoryException {
  }

  @Override
  public Iterator<String> getPropertyNames() throws RepositoryException {
    return null;
  }

  @Override
  public Iterator<String> getPropertyNames(String relPath) throws RepositoryException {
    return null;
  }

  @Override
  public boolean hasProperty(String relPath) throws RepositoryException {
    return false;
  }

  @Override
  public void setProperty(String relPath, Value value) throws RepositoryException {
  }

  @Override
  public void setProperty(String relPath, Value[] value) throws RepositoryException {
  }

  @Override
  public Value[] getProperty(String relPath) throws RepositoryException {
    return null;
  }

  @Override
  public boolean removeProperty(String relPath) throws RepositoryException {
    return false;
  }

  @Override
  public String getPath() throws RepositoryException {
    return null;
  }

  @Override
  public boolean isAdmin() {
    return false;
  }

  @Override
  public boolean isSystemUser() {
    return false;
  }

  @Override
  public Credentials getCredentials() throws RepositoryException {
    return null;
  }

  @Override
  public Impersonation getImpersonation() throws RepositoryException {
    return null;
  }

  @Override
  public void changePassword(String password) throws RepositoryException {
  }

  @Override
  public void changePassword(String password, String oldPassword) throws RepositoryException {
  }

  @Override
  public void disable(String reason) throws RepositoryException {
  }

  @Override
  public boolean isDisabled() throws RepositoryException {
    return false;
  }

  @Override
  public String getDisabledReason() throws RepositoryException {
    return null;
  }

}
