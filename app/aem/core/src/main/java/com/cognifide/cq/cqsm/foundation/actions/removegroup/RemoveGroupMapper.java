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
package com.cognifide.cq.cqsm.foundation.actions.removegroup;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.cq.cqsm.foundation.actions.ActionGroup;
import java.util.Collections;
import java.util.List;

@Mapper(value = "remove-group", group = ActionGroup.CORE)
public final class RemoveGroupMapper {

  public static final String REFERENCE = "Remove specified groups.\n"
      + "No group's members are removed, but they no longer belong to the removed group (reference is removed).\n"
      + "Note that no permissions for removed group are cleaned, so after creating a new group with the same id"
      + " - it will automatically gain those permissions.";

  @Mapping(
      examples = "REMOVE-GROUP 'authors'",
      reference = REFERENCE
  )
  public Action mapAction(@Required(value = "groupId", description = "group's id e.g.: 'authors'") String id) {
    return mapAction(Collections.singletonList(id));
  }

  @Mapping(
      examples = "REMOVE-GROUP ['authors']",
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "groupIds", description = "groups' ids e.g.: ['authors']") List<String> ids) {
    return new RemoveGroup(ids);
  }

}
