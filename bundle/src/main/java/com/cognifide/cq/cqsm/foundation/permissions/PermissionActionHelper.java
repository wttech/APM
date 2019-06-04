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
package com.cognifide.cq.cqsm.foundation.permissions;

import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.foundation.permissions.exceptions.PermissionException;
import com.cognifide.cq.cqsm.foundation.permissions.utils.JackrabbitAccessControlListUtil;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;

public class PermissionActionHelper {

  private final ValueFactory valueFactory;

  private final String path;

  private final List<String> permissions;

  private final Restrictions restrictions;

  public PermissionActionHelper(ValueFactory valueFactory, String path, List<String> permissions,
      Restrictions restrictions) {
    this.valueFactory = valueFactory;
    this.path = path;
    this.permissions = permissions;
    this.restrictions = restrictions;
  }

  public void checkPermissions(AccessControlManager accessControlManager)
      throws RepositoryException, PermissionException {
    createPrivileges(accessControlManager, permissions);
  }

  public void applyPermissions(AccessControlManager accessControlManager, Principal principal,
      boolean allow) throws RepositoryException, PermissionException {
    final List<Privilege> privileges = createPrivileges(accessControlManager, permissions);
    updateAccessControlList(allow, accessControlManager, privileges, principal);
  }

  private void updateAccessControlList(boolean allow,
      final AccessControlManager accessControlManager,
      final List<Privilege> privileges, final Principal principal) throws RepositoryException {
    final JackrabbitAccessControlList jackrabbitAcl = JackrabbitAccessControlListUtil
        .getModifiableAcl(accessControlManager, path);

    addEntry(allow, privileges, principal, jackrabbitAcl);
    accessControlManager.setPolicy(path, jackrabbitAcl);
  }

  private void addEntry(boolean allow, final List<Privilege> privileges,
      final Principal principal,
      final JackrabbitAccessControlList jackrabbitAcl) throws RepositoryException {

    Map<String, Value> singleValueRestrictions = restrictions.getSingleValueRestrictions(valueFactory);
    Map<String, Value[]> multiValueRestrictions = restrictions.getMultiValueRestrictions(valueFactory);
    jackrabbitAcl.addEntry(principal, privileges.toArray(new Privilege[privileges.size()]), allow,
        singleValueRestrictions, multiValueRestrictions);
  }

  public List<Privilege> createPrivileges(final AccessControlManager accessControlManager,
      final List<String> permissions) throws RepositoryException, PermissionException {
    final List<Privilege> privileges = new ArrayList<>();
    final List<String> unknownPermissions = new ArrayList<>();
    for (final String permission : permissions) {
      try {
        privileges.addAll(createPrivileges(accessControlManager, permission));
      } catch (PermissionException e) {
        unknownPermissions.add(permission);
      }
    }
    if (!unknownPermissions.isEmpty()) {
      throw new PermissionException(MessagingUtils.unknownPermissions(unknownPermissions));
    }

    return privileges;
  }

  private List<Privilege> createPrivileges(final AccessControlManager accessControlManager,
      final String permission) throws RepositoryException, PermissionException {
    try {
      final PrivilegeList privilegeList = PrivilegeList.getFromTitle(permission);
      return privilegeList.createPrivileges(accessControlManager);
    } catch (IllegalArgumentException e) {
      throw new PermissionException("Unknown permission " + permission, e);
    }
  }

}
