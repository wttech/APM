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
package com.cognifide.apm.main.actions.clearpermissions;

import static com.cognifide.apm.main.actions.clearpermissions.ClearPermissionsMapper.REFERENCE;
import static com.cognifide.apm.main.actions.clearpermissions.ClearPermissionsMapper.STRICT_PATH;
import static com.cognifide.apm.main.actions.clearpermissions.ClearPermissionsMapper.STRICT_PATH_DESC;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;

@Mapper(value = "clear", group = ActionGroup.CORE)
public final class ClearMapper {

  @Mapping(
      examples = {
          "CLEAR '/'",
          "CLEAR '/' --" + STRICT_PATH
      },
      reference = REFERENCE + " Alias for CLEAR-PERMISSIONS command."
  )
  public Action mapAction(
      @Required(value = "path", description = "e.g.: '/content/dam'") String path,
      @Flag(value = STRICT_PATH, description = STRICT_PATH_DESC) boolean strictPath) {
    if (strictPath) {
      return new RemoveAll(path);
    } else {
      return new Purge(path);
    }
  }

}
