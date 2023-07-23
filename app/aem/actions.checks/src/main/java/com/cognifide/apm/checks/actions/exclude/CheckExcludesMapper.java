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
package com.cognifide.apm.checks.actions.exclude;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.checks.actions.ActionGroup;
import java.util.Collections;
import java.util.List;

@Mapper(value = "CHECK-EXCLUDES", group = ActionGroup.CHECKS)
public final class CheckExcludesMapper {

  public static final String REFERENCE = "Verify that provided group DOES NOT contain any of listed authorizables.";

  @Mapping(
      examples = "CHECK-EXCLUDES 'authors' 'author'",
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "group", description = "group's id e.g.: 'authors'") String group,
      @Required(value = "id", description = "users' or groups' id e.g.: 'author'") String id) {
    return mapAction(group, Collections.singletonList(id));
  }

  @Mapping(
      examples = "CHECK-EXCLUDES 'authors' ['author']",
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "group", description = "group's id e.g.: 'authors'") String group,
      @Required(value = "ids", description = "users' or groups' ids e.g.: ['author']") List<String> ids) {
    return new CheckExcludes(group, ids);
  }
}
