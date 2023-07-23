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
package com.cognifide.apm.checks.actions.property;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.checks.actions.ActionGroup;

@Mapper(value = "CHECK-PROPERTY", group = ActionGroup.CHECKS)
public final class CheckPropertyMapper {

  public static final String REFERENCE = "Verify that a property is set to specific value for given authorizable.";

  @Mapping(
      examples = "CHECK-PROPERTY 'author' 'title' 'John Doe'",
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "id", description = "users' or groups' id e.g.: 'author'") String id,
      @Required(value = "name", description = "property name") String name,
      @Required(value = "value", description = "property value") String value) {
    return new CheckProperty(id, name, value);
  }
}
