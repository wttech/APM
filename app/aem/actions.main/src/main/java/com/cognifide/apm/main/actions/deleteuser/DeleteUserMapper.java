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
package com.cognifide.apm.main.actions.deleteuser;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.CompositeAction;
import com.cognifide.apm.main.actions.ActionGroup;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(value = "DELETE-USER", group = ActionGroup.CORE)
public final class DeleteUserMapper {

  public static final String CLEAR_PERMISSIONS = "CLEAR-PERMISSIONS";
  public static final String CLEAR_PERMISSIONS_DESC = "additionally clears permissions related to given user";

  public static final String REFERENCE = "Remove specified users.\n"
      + "Removed user are no longer listed as any group members.\n"
      + "Note that no permissions for removed users are cleaned, so after creating a new user with the same id"
      + " - it will automatically gain those permissions.";

  @Mapping(
      examples = {
          "DELETE-USER 'author'",
          "DELETE-USER 'author' --" + CLEAR_PERMISSIONS
      },
      reference = REFERENCE
  )
  public Action create(
      @Required(value = "userId", description = "user's id e.g.: 'author'") String id,
      @Flag(value = CLEAR_PERMISSIONS, description = CLEAR_PERMISSIONS_DESC) boolean clearPermissions) {
    if (clearPermissions) {
      return new DestroyUser(id);
    } else {
      return new RemoveUser(Collections.singletonList(id));
    }
  }

  @Mapping(
      examples = {
          "DELETE-USER ['author']",
          "DELETE-USER ['author'] --" + CLEAR_PERMISSIONS
      },
      reference = REFERENCE
  )
  public Action create(
      @Required(value = "userIds", description = "users' ids e.g.: ['author']") List<String> ids,
      @Flag(value = CLEAR_PERMISSIONS, description = CLEAR_PERMISSIONS_DESC) boolean clearPermissions) {
    if (clearPermissions) {
      List<Action> actions = ids.stream().map(DestroyUser::new).collect(Collectors.toList());
      return new CompositeAction(actions);
    } else {
      return new RemoveUser(ids);
    }
  }
}
