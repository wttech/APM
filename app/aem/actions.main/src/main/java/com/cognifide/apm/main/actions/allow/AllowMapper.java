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
package com.cognifide.apm.main.actions.allow;

import static com.cognifide.apm.main.actions.CommonFlags.IF_EXISTS;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Named;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;
import java.util.List;
import java.util.Map;

@Mapper(value = "ALLOW", group = ActionGroup.CORE)
public class AllowMapper {

  public static final String REFERENCE = "Add allow permissions for current authorizable on specified path. "
      + "Script fails if path doesn't exist.";

  @Mapping(
      examples = {
          "ALLOW '/content/dam' [READ]",
          "ALLOW '/content/dam' [MODIFY] glob='/*'",
          "ALLOW '/content/dam' [MODIFY] properties=['jcr:title']",
          "ALLOW '/content/dam' [MODIFY] types=['nt:folder']",
          "ALLOW '/content/dam' [MODIFY] restrictions={'restriction1': 'value', 'restriction2': ['value1', 'value2']}",
          "ALLOW '/content/dam/domain' [READ, MODIFY] --IF-EXISTS"
      },
      reference = REFERENCE
  )
  public Action create(
      @Required(value = "path", description = "e.g.: '/content/dam'") String path,
      @Required(value = "permissions", description = "e.g.: [READ, 'jcr:all']") List<String> permissions,
      @Named(value = "glob", description = "regular expression to narrow set of paths") String glob,
      @Named(value = "types", description = "list of jcr types which will be affected") List<String> types,
      @Named(value = "properties", description = "list of properties which will be affected") List<String> items,
      @Named(value = "restrictions", description = "map of custom restrictions") Map<String, Object> restrictions,
      @Flag(value = IF_EXISTS, description = "script doesn't fail if path doesn't exist") boolean ifExists) {
    return new Allow(path, permissions, glob, types, items, restrictions, ifExists);
  }

  @Mapping(
      examples = {
          "ALLOW [READ] ON '/content/dam'",
          "ALLOW [MODIFY] ON '/content/dam' glob='/*'",
          "ALLOW [MODIFY] ON '/content/dam' properties=['jcr:title']",
          "ALLOW [MODIFY] ON '/content/dam' types=['nt:folder']",
          "ALLOW [MODIFY] ON '/content/dam' restrictions={'restriction1': 'value', 'restriction2': ['value1', 'value2']}",
          "ALLOW [READ, MODIFY] ON '/content/dam/domain' --IF-EXISTS"
      },
      reference = REFERENCE
  )
  public Action create(
      @Required(value = "permissions", description = "e.g.: [READ, 'jcr:all']") List<String> permissions,
      @Required(value = "path", description = "e.g.: '/content/dam'") String path,
      @Named(value = "glob", description = "regular expression to narrow set of paths") String glob,
      @Named(value = "types", description = "list of jcr types which will be affected") List<String> types,
      @Named(value = "properties", description = "list of properties which will be affected") List<String> items,
      @Named(value = "restrictions", description = "map of custom restrictions") Map<String, Object> restrictions,
      @Flag(value = IF_EXISTS, description = "script doesn't fail if path doesn't exist") boolean ifExists) {
    return new Allow(path, permissions, glob, types, items, restrictions, ifExists);
  }
}
