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

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import java.util.Collections;
import java.util.List;

@Mapper("remove_group")
public final class RemoveGroupMapper {

  public static final String REFERENCE = "Remove specified groups.\n"
      + "No group's members are removed, but they no longer belong to the removed group (reference is removed).\n"
      + "Note that no permissions for removed group are cleaned, so after creating a new group with the same id"
      + " - it will automatically gain those permissions.";

  @Mapping(
      args = {"groupId"},
      reference = REFERENCE
  )
  public Action mapAction(String id) {
    return mapAction(Collections.singletonList(id));
  }

  @Mapping(
      args = {"groupIds"},
      reference = REFERENCE
  )
  public Action mapAction(final List<String> ids) {
    return new RemoveGroup(ids);
  }

}
