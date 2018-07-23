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

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.foundation.actions.Flag;

@Mapper("create_user")
public class CreateUserMapper {

  public static final String REFERENCE = "Create a user.";

  @Mapping(
      args = {"userId", "password", "path", Flag.IF_NOT_EXISTS},
      reference = REFERENCE
  )
  public Action mapAction(String id, String password, String path, String flag) {
    return mapAction(id, password, path, Flag.isIfNotExists(flag));
  }

  @Mapping(
      args = {"userId", "password"},
      reference = REFERENCE
  )
  public Action mapAction(String id, String password) {
    return mapAction(id, password, null, false);
  }

  @Mapping(
      args = {"userId"},
      reference = REFERENCE
  )
  public Action mapAction(String id) {
    return mapAction(id, null, null, false);
  }

  private Action mapAction(String id, String password, String path, Boolean ifNotExists) {
    return new CreateAuthorizable(id, password, path, ifNotExists, CreateAuthorizableStrategy.USER);
  }

}
