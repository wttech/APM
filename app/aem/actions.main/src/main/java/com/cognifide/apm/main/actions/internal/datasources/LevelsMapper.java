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

@Mapper(value = "LEVELS", group = ActionGroup.DATASOURCE)
public class LevelsMapper {

  @Mapping(
      examples = "LEVELS('/content', [<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;{regex: '(.+)_(.+)',paramNames: ['param1', 'param2']}, # 1st level<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;{excludeRegex: '[^:]+'}, # 2nd level<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;{template: '/apps/test/pageTemplate', resourceType: 'test/pageRenderer'}, # 3rd level<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;{properties: [ # 4th level<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{name: 'jcr:primaryType', regex: 'cq:Page'},<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{name: 'jcr:primaryType', excludeRegex: 'cq:PageContent'},<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{name: 'jcr:content/cq:template', regex: '/apps/test/pageTemplate'},<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{name: 'jcr:content/sling:resourceType', regex: 'test/pageRenderer'}<br>" +
          "&nbsp;&nbsp;&nbsp;&nbsp;]}<br>" +
          "])",
      reference = "Provides levels of content for given resource path matching given content structure map"
  )
  public Action mapAction(
      @Required(value = "rootPath", description = "Root path") String rootPath,
      @Required(value = "structureMap", description = "Map of content structure") List<Object> structureMap) {
    throw new NotImplementedException("");
  }
}
