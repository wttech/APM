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
package com.cognifide.apm.main.utils;

import com.cognifide.apm.api.scripts.Script;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class MessagingUtils {

  private MessagingUtils() {
  }

  public static String createMessage(Exception e) {
    return StringUtils.isBlank(e.getMessage()) ? "Internal error: " + e.getClass() : e.getMessage();
  }

  public static String removedFromGroup(String authorizableId, String groupId) {
    return "Authorizable " + authorizableId + " removed from group " + groupId;
  }

  public static String addedToGroup(String authorizableId, String groupId) {
    return "Authorizable " + authorizableId + " added to group " + groupId;
  }

  public static String failedToAddToGroup(String authorizableId, String groupId) {
    return String.format("Failed to add authorizable %s to group %s (some specific constraint)", authorizableId, groupId);
  }

  public static String newPasswordSet(String userId) {
    return "New password for user " + userId + " was set";
  }

  public static String addingGroupToItself(String groupId) {
    return "You can not add group " + groupId + " to itself";
  }

  public static String authorizableNotExists(String authorizableId) {
    return "Authorizable with id: " + authorizableId + " does not exists";
  }

  public static String groupHasNoMembers(String groupId) {
    return "Group with id: " + groupId + " has no members.";
  }

  public static String groupIsMemberOfNoGroups(String groupId) {
    return "Group with id: " + groupId + " is a member of no groups.";
  }

  public static String cyclicRelationsForbidden(String currentGroup, String groupToBeAdded) {
    return "Cannot add group " + groupToBeAdded + " to group " + currentGroup
        + " due to resulting cyclic relation";
  }

  public static String unknownPermissions(List<String> permissions) {
    if (permissions.size() == 1) {
      return "Unknown permission: " + permissions.get(0);
    }
    StringBuilder result = new StringBuilder();
    result.append("Unknown permissions: ");
    Iterator<String> it = permissions.iterator();

    while (it.hasNext()) {
      result.append(it.next());
      if (it.hasNext()) {
        result.append(", ");
      }
    }

    return result.toString();
  }

  public static String describeScripts(List<Script> scripts) {
    List<String> paths = new LinkedList<>();

    for (Script script : scripts) {
      paths.add(script.getPath());
    }

    return String.join("\n", paths);
  }

}
