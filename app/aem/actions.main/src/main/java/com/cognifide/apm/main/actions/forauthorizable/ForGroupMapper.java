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
package com.cognifide.apm.main.actions.forauthorizable;

import static com.cognifide.apm.main.actions.CommonFlags.IF_EXISTS;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;

@Mapper(value = "FOR-GROUP", group = ActionGroup.CORE)
public final class ForGroupMapper {

  @Mapping(
      examples = {
          "FOR-GROUP 'authors' BEGIN ... END",
          "FOR-GROUP 'authors' --IF-EXISTS BEGIN ... END"
      },
      reference = "Set specified group as a current authorizable for execution context."
  )
  public Action mapAction(@Required(value = "groupId", description = "group's id e.g.: 'authors'") String groupId,
      @Flag(value = IF_EXISTS, description = "script doesn't fail if group doesn't exist") boolean ifExists) {
    return new ForAuthorizable(groupId, ifExists, true);
  }
}
