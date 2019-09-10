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
package com.cognifide.cq.cqsm.foundation.actions.allow;

import static com.cognifide.cq.cqsm.foundation.actions.CommonFlags.IF_EXISTS;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Flags;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.actions.annotations.Named;
import java.util.List;

@Mapper("allow")
public class AllowMapper {

  public static final String REFERENCE = "Add allow permissions for current authorizable on specified path.";

  @Mapping(
      value = "ALLOW",
      args = {"path", "permissions", "glob", "types", "items"},
      reference = REFERENCE
  )
  public Action create(String path, List<String> permissions,
      @Named("glob") String glob, @Named("types") List<String> types, @Named("properties") List<String> items,
      @Flags List<String> flags) {
    return new Allow(path, permissions, glob, types, items, flags.contains(IF_EXISTS));
  }
}
