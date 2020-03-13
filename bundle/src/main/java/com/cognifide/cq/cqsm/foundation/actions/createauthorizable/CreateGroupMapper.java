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
package com.cognifide.cq.cqsm.foundation.actions.createauthorizable;

import static com.cognifide.cq.cqsm.foundation.actions.CommonFlags.IGNORE_IF_EXISTS;
import static com.cognifide.cq.cqsm.foundation.actions.createauthorizable.CreateAuthorizableStrategy.GROUP;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Flag;
import com.cognifide.cq.cqsm.api.actions.annotations.Flags;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.actions.annotations.Named;
import com.cognifide.cq.cqsm.api.actions.annotations.Required;
import com.cognifide.cq.cqsm.foundation.actions.ActionGroup;
import java.util.List;

@Mapper(value = "create-group", group = ActionGroup.CORE)
public class CreateGroupMapper {

  public static final String REFERENCE = "Create a group. Script fails if user already exists.";

  @Mapping(
      examples = {
          "CREATE-GROUP 'authors'",
          "CREATE-GROUP 'authors' path= '/home/users/client/domain' --IGNORE-IF-EXISTS"
      },
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "groupId", description = "group's id e.g.: 'authors'") String groupId,
      @Named(value = "path", description = "group's home e.g.: '/home/groups/domain'") String path,
      @Flags({
          @Flag(value = IGNORE_IF_EXISTS, description = "script doesn't fail if group already exists")}) List<String> flags) {
    return new CreateAuthorizable(groupId, null, path, flags.contains(IGNORE_IF_EXISTS), GROUP);
  }

}
