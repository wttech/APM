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
package com.cognifide.apm.checks.utils;

import org.apache.commons.lang3.StringUtils;

public final class MessagingUtils {

  private MessagingUtils() {
    // intentionally empty
  }

  public static String createMessage(Exception e) {
    return StringUtils.isBlank(e.getMessage()) ? "Internal error: " + e.getClass() : e.getMessage();
  }

  public static String addingGroupToItself(String groupId) {
    return "You can not add group " + groupId + " to itself";
  }

  public static String authorizableNotExists(String authorizableId) {
    return "Authorizable with id: " + authorizableId + " does not exists";
  }

  public static String cyclicRelationsForbidden(String currentGroup, String groupToBeAdded) {
    return "Cannot add group " + groupToBeAdded + " to group " + currentGroup
        + " due to resulting cyclic relation";
  }
}
