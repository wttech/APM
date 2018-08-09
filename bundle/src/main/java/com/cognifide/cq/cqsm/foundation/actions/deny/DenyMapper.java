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
package com.cognifide.cq.cqsm.foundation.actions.deny;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.foundation.actions.Flag;
import java.util.List;

@Mapper("deny")
public final class DenyMapper {

  public static final String REFERENCE = "This action is an complementary one for ALLOW action, and can be used to"
      + " add deny permission for current authorizable on specified path.";

  @Mapping(
      args = {"path", "glob", "permissions", Flag.IF_EXISTS},
      reference = REFERENCE,
      order = 1
  )
  public Action mapAction(String path, String glob, List<String> permissions, String flag) {
    return mapAction(path, glob, permissions, Flag.isIfExists(flag));
  }

  @Mapping(
      args = {"path", "glob", "permissions"},
      reference = REFERENCE,
      order = 2
  )
  public Action mapAction(String path, String glob, List<String> permissions) {
    return mapAction(path, glob, permissions, false);
  }

  @Mapping(
      args = {"path", "permissions", Flag.IF_EXISTS},
      reference = REFERENCE,
      order = 3
  )
  public Action mapAction(String path, List<String> permissions, String flag) {
    return mapAction(path, null, permissions, Flag.isIfExists(flag));
  }

  @Mapping(
      args = {"path", "permissions"},
      reference = REFERENCE,
      order = 4
  )
  public Action mapAction(String path, List<String> permissions) {
    return mapAction(path, null, permissions, false);
  }

  private Action mapAction(String path, String glob, List<String> permissions, Boolean ifExists) {
    return new Deny(path, glob, ifExists, permissions);
  }
}
