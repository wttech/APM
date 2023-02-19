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
package com.cognifide.apm.main.actions.createauthorizable;

import static com.cognifide.apm.main.actions.CommonFlags.ERROR_IF_EXISTS;
import static com.cognifide.apm.main.actions.createauthorizable.CreateAuthorizableStrategy.GROUP;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Named;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;

@Mapper(value = "CREATE-GROUP", group = ActionGroup.CORE)
public class CreateGroupMapper {

  public static final String REFERENCE = "Create a group. Script fails if group already exists.";

  @Mapping(
      examples = {
          "CREATE-GROUP 'authors'",
          "CREATE-GROUP 'authors' path='/home/groups/client/domain' --ERROR-IF-EXISTS",
          "CREATE-GROUP 'authors' path='/home/groups/client/domain' externalId='authors'"
      },
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "groupId", description = "group's id e.g.: 'authors'") String groupId,
      @Named(value = "path", description = "group's home e.g.: '/home/groups/client/domain'") String path,
      @Named(value = "externalId", description = "group's external id e.g.: 'authors'") String externalId,
      @Flag(value = ERROR_IF_EXISTS, description = "if group already exists, raise an error and stop script execution") boolean errorIfExists) {
    return new CreateAuthorizable(groupId, null, path, externalId, !errorIfExists, GROUP);
  }

}
