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
package com.cognifide.apm.checks.actions.exists;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Named;
import com.cognifide.apm.checks.actions.ActionGroup;

@Mapper(value = "check-group-exists", group = ActionGroup.CHECKS)
public final class CheckGroupExistsMapper {

  public static final String REFERENCE = "Verify that provided authorizable exists and is a group."
      + " Optionally it can be used to verify that given group resides in specific path.";

  @Mapping(
      reference = REFERENCE
  )
  public Action mapAction(String id, @Named("path") String path) {
    return new CheckAuthorizableExists(id, path, true);
  }

}
