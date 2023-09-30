/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.main.actions.internal.datasources;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;

@Mapper(value = "CONTENT", group = ActionGroup.DATASOURCE)
public class ContentMapper {

  @Mapping(
      examples = "CONTENT('/content/(.+)/(.+)/(.+)', ['brand', 'country', 'language'])",
      reference = "Provides content structure for given resource path matching given path regex and parameters names"
  )
  public Action mapAction(
      @Required(value = "pathRegex", description = "Path regex expression") String pathRegex,
      @Required(value = "paramNames", description = "List of parameters names") List<String> paramNames) {
    throw new NotImplementedException("");
  }
}
