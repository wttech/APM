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
package com.cognifide.cq.cqsm.foundation.actions.clearfromgroups;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;

@Mapper("clear_from_groups")
public final class ClearFromGroupsMapper {

  @Mapping(
      reference = "This action removes all memberships of a given group."
  )
  public Action mapAction() {
    return new ClearFromGroups(ClearFromGroupOperationTypes.ALL_PARENTS);
  }

  @Mapping(
      args = {"mode"},
      reference = "If mode equals all-children, it removes given group membership from child groups.\n"
          + "Otherwise it removes all memberships of a given group."
  )
  public Action mapActionWithAllChildren(String mode) {
    return new ClearFromGroups(toOperationType(mode));
  }

  private ClearFromGroupOperationTypes toOperationType(String mode) {
    if ("ALL-CHILDREN".equalsIgnoreCase(mode)) {
      return ClearFromGroupOperationTypes.ALL_CHILDREN;
    }
    return ClearFromGroupOperationTypes.ALL_PARENTS;
  }
}
