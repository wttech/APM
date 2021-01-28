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
import com.cognifide.apm.checks.actions.ActionGroup;
import java.util.Collections;
import java.util.List;

@Mapper(value = "check-excludes", group = ActionGroup.CHECKS)
public final class CheckExcludesMapper {

  public static final String REFERENCE = "Verify that provided group DOES NOT contain any of listed authorizables.";

  @Mapping(
      reference = REFERENCE
  )
  public Action mapAction(String group, String id) {
    return mapAction(group, Collections.singletonList(id));
  }

  @Mapping(
      reference = REFERENCE
  )
  public Action mapAction(String group, List<String> ids) {
    return new CheckExcludes(group, ids);
  }
}
