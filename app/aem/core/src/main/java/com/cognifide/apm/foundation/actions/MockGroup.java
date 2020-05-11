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

import com.google.common.collect.Iterators;
import java.security.Principal;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;

public class MockGroup implements Group {

  private final String id;

  public MockGroup(final String id) {
    this.id = id;
  }

  @Override
  public String getID() throws RepositoryException {
    return id;
  }

  @Override
  public boolean isGroup() {
    return true;
  }

  @Override
  public Principal getPrincipal() throws RepositoryException {
    return new MockPrincipal(id);
  }

  @Override
  public Iterator<Group> declaredMemberOf() throws RepositoryException {
    return Iterators.emptyIterator();
  }

  @Override
  public Iterator<Group> memberOf() throws RepositoryException {
    return Iterators.emptyIterator();
  }

  @Override
  public void remove() throws RepositoryException {
  }

  @Override
  public Iterator<String> getPropertyNames() throws RepositoryException {
    return Iterators.emptyIterator();
  }

  @Override
  public Iterator<String> getPropertyNames(String relPath) throws RepositoryException {
    return Iterators.emptyIterator();
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
  public Iterator<Authorizable> getDeclaredMembers() throws RepositoryException {
    return Iterators.emptyIterator();
  }

  @Override
  public Iterator<Authorizable> getMembers() throws RepositoryException {
    return Iterators.emptyIterator();
  }

  @Override
  public boolean isDeclaredMember(Authorizable authorizable) throws RepositoryException {
    return false;
  }

  @Override
  public boolean isMember(Authorizable authorizable) throws RepositoryException {
    return false;
  }

  @Override
  public boolean addMember(Authorizable authorizable) throws RepositoryException {
    return false;
  }

  @Override
  public Set<String> addMembers(String... strings) throws RepositoryException {
    return Collections.emptySet();
  }

  @Override
  public boolean removeMember(Authorizable authorizable) throws RepositoryException {
    return false;
  }

  @Override
  public Set<String> removeMembers(String... strings) throws RepositoryException {
    return Collections.emptySet();
  }

}