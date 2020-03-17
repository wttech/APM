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

import static com.cognifide.cq.cqsm.foundation.actions.CommonFlags.IF_NOT_EXISTS;
import static com.cognifide.cq.cqsm.foundation.actions.createauthorizable.CreateAuthorizableStrategy.USER;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Flag;
import com.cognifide.cq.cqsm.api.actions.annotations.Flags;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.actions.annotations.Named;
import com.cognifide.cq.cqsm.api.actions.annotations.Required;
import com.cognifide.cq.cqsm.foundation.actions.ActionGroup;
import java.util.List;

@Mapper(value = "create-user", group = ActionGroup.CORE)
public class CreateUserMapper {

  public static final String REFERENCE = "Create a user. Script fails if user already exists.";

  @Mapping(
      examples = {
          "CREATE-USER 'author'",
          "CREATE-USER 'author' password= 'p@$$w0rd' --IF-NOT-EXISTS",
          "CREATE-USER 'author' path= '/home/users/client/domain'"
      },
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "userId", description = "user's login e.g.: 'author'") String userId,
      @Named(value = "password", description = "user's password e.g.: 'p@$$w0rd'") String password,
      @Named(value = "path", description = "user's home e.g.: '/home/users/domain'") String path,
      @Flags({@Flag(value = IF_NOT_EXISTS, description = "action is executed only if user doesn't exist, "
          + "and script doesn't fail in that case")}) List<String> flags) {
    return new CreateAuthorizable(userId, password, path, flags.contains(IF_NOT_EXISTS), USER);
  }

}
