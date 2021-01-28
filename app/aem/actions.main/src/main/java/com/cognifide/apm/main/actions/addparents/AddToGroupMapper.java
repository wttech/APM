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
package com.cognifide.apm.main.actions.addparents;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;
import java.util.Collections;
import java.util.List;

@Mapper(value = "add-to-group", group = ActionGroup.CORE)
public final class AddToGroupMapper {

  public static final String REFERENCE = "Add current authorizable to specified groups. "
      + "Alias for ADD-PARENTS command.";

  @Mapping(
      examples = "ADD-TO-GROUP 'authors'",
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "group", description = "group") String group) {
    return mapAction(Collections.singletonList(group));
  }

  @Mapping(
      examples = "ADD-TO-GROUP ['authors', 'publishers']",
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "groups", description = "list of groups") List<String> groups) {
    return new AddParents(groups);
  }
}
