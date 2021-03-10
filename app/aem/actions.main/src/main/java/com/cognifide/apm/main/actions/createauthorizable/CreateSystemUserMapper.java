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
package com.cognifide.apm.main.actions.createauthorizable;

import static com.cognifide.apm.main.actions.CommonFlags.ERROR_IF_EXISTS;
import static com.cognifide.apm.main.actions.createauthorizable.CreateAuthorizableStrategy.SYSTEM_USER;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Named;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;

@Mapper(value = "create-system-user", group = ActionGroup.CORE)
public class CreateSystemUserMapper {

  public static final String REFERENCE = "Create a system user. Script fails if user already exists.";

  @Mapping(
      examples = {
          "CREATE-SYSTEM-USER 'apm-user'",
          "CREATE-SYSTEM-USER 'apm-user' --ERROR-IF-EXISTS",
          "CREATE-SYSTEM-USER 'apm-user' path= '/home/users/client/domain'",
          "CREATE-SYSTEM-USER 'apm-user' path= '/home/users/client/domain' BEGIN\n" +
              " SET-PROPERTY 'first-name' 'APM'\n" +
              "END"
      },
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "userId", description = "user's login e.g.: 'apm-user'") String userId,
      @Named(value = "path", description = "user's home e.g.: '/home/users/domain'") String path,
      @Flag(value = ERROR_IF_EXISTS, description = "if user already exists, raise an error and stop script execution") boolean errorIfExists) {
    return new CreateAuthorizable(userId, null, path, !errorIfExists, SYSTEM_USER);
  }

}
