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
import static com.cognifide.apm.main.actions.createauthorizable.CreateAuthorizableStrategy.USER;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Named;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;

@Mapper(value = "CREATE-USER", group = ActionGroup.CORE)
public class CreateUserMapper {

  public static final String REFERENCE = "Create a user. If user already exists, the following block of code will be skipped.";

  @Mapping(
      examples = {
          "CREATE-USER 'author'",
          "CREATE-USER 'author' password='p@$$w0rd' --ERROR-IF-EXISTS",
          "CREATE-USER 'author' path='/home/users/client/domain'",
          "CREATE-USER 'author' path='/home/users/client/domain' BEGIN\n" +
              " SET-PROPERTY 'first-name' 'Author'\n" +
              "END"
      },
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "userId", description = "user's login e.g.: 'author'") String userId,
      @Named(value = "password", description = "user's password e.g.: 'p@$$w0rd'") String password,
      @Named(value = "path", description = "user's home e.g.: '/home/users/client/domain'") String path,
      @Flag(value = ERROR_IF_EXISTS, description = "if user already exists, raise an error and stop script execution") boolean errorIfExists) {
    return new CreateAuthorizable(userId, password, path, null, !errorIfExists, USER);
  }

}
