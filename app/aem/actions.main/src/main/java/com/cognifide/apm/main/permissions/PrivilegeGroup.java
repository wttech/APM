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
package com.cognifide.apm.main.permissions;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

public enum PrivilegeGroup {

  READ("READ", Privilege.JCR_READ),

  MODIFY("MODIFY",
      Privilege.JCR_MODIFY_PROPERTIES,
      Privilege.JCR_LOCK_MANAGEMENT,
      Privilege.JCR_VERSION_MANAGEMENT),

  MODIFY_PAGE("MODIFY_PAGE",
      Privilege.JCR_REMOVE_NODE,
      Privilege.JCR_REMOVE_CHILD_NODES,
      Privilege.JCR_NODE_TYPE_MANAGEMENT,
      Privilege.JCR_ADD_CHILD_NODES),

  CREATE("CREATE",
      Privilege.JCR_ADD_CHILD_NODES,
      Privilege.JCR_NODE_TYPE_MANAGEMENT),

  DELETE("DELETE",
      Privilege.JCR_REMOVE_NODE,
      Privilege.JCR_REMOVE_CHILD_NODES),

  REPLICATE("REPLICATE", "crx:replicate"),

  ALL("ALL",
      Privilege.JCR_READ,
      Privilege.JCR_WRITE,
      Privilege.JCR_LOCK_MANAGEMENT,
      Privilege.JCR_VERSION_MANAGEMENT,
      Privilege.JCR_NODE_TYPE_MANAGEMENT,
      "crx:replicate"),

  READ_ACL("READ_ACL", Privilege.JCR_READ_ACCESS_CONTROL),

  MODIFY_ACL("MODIFY_ACL", Privilege.JCR_MODIFY_ACCESS_CONTROL),

  DELETE_CHILD_NODES("DELETE_CHILD_NODES", Privilege.JCR_REMOVE_CHILD_NODES);

  private final String title;

  private final List<String> privileges;

  PrivilegeGroup(String title, String... privileges) {
    this.title = title;
    this.privileges = ImmutableList.copyOf(privileges);
  }

  public static Optional<PrivilegeGroup> getFromTitle(String title) {
    return Arrays.stream(PrivilegeGroup.values())
        .filter(it -> it.getTitle().equalsIgnoreCase(title))
        .findFirst();
  }

  public String getTitle() {
    return title;
  }

  public List<Privilege> toPrivileges(AccessControlManager accessControlManager)
      throws RepositoryException {
    final List<Privilege> result = new ArrayList<>();
    for (String privilege : privileges) {
      result.add(accessControlManager.privilegeFromName(privilege));
    }
    return result;
  }
}
