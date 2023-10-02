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
package com.cognifide.apm.core.grammar.datasource;

import com.cognifide.apm.core.grammar.ApmList;
import com.cognifide.apm.core.grammar.ApmString;
import com.cognifide.apm.core.grammar.ApmType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component
public class GroupsDataSource implements DataSource {

  @Override
  public String getName() {
    return "GROUPS";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<ApmType> parameters) {
    String value = parameters.get(0).getString();
    String regex = parameters.get(1).getString();
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);
    List<ApmType> list = new ArrayList<>();
    if (matcher.matches()) {
      for (int i = 0; i <= matcher.groupCount(); i++) {
        list.add(new ApmString(matcher.group(i)));
      }
    }
    return new ApmList(list);
  }
}
