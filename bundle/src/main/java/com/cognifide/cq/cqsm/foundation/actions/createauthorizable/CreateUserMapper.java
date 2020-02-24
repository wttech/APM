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
import static com.cognifide.cq.cqsm.foundation.actions.createauthorizable.CreateAuthorizableStrategy.USER;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Flag;
import com.cognifide.cq.cqsm.api.actions.annotations.Flags;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.actions.annotations.Named;
import com.cognifide.cq.cqsm.api.actions.annotations.Required;
import java.util.List;

@Mapper("create-user")
public class CreateUserMapper {

  public static final String REFERENCE = "Create a user. Script fails if user already exists.";

  @Mapping(
      value = "CREATE-USER",
      examples = {
          "CREATE-USER 'client.author'",
          "CREATE-USER 'client.author' password= 'p@$$w0rd' --IGNORE-IF-EXISTS",
          "CREATE-USER 'client.author' path= '/home/users/client/domain'"
      },
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "userId", description = "user's login e.g.: 'client.author'") String userId,
      @Named(value = "password", description = "user's password e.g.: 'p@$$w0rd'") String password,
      @Named(value = "path", description = "user's home e.g.: '/home/users/client.domain'") String path,
      @Flags({
          @Flag(value = IGNORE_IF_EXISTS, description = "script doesn't fail if user already exists")}) List<String> flags) {
    return new CreateAuthorizable(userId, password, path, flags.contains(IGNORE_IF_EXISTS), USER);
  }

}
