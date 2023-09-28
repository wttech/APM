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
import com.cognifide.apm.core.grammar.ApmMap;
import com.cognifide.apm.core.grammar.ApmString;
import com.cognifide.apm.core.grammar.ApmType;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class NodesDataSource implements DataSource {

  @Override
  public String getName() {
    return "NODES";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<ApmType> parameters) {
    String path = parameters.get(0).getString();
    String regex = parameters.size() >= 2 ? parameters.get(1).getString() : "[^:]+";
    Pattern pattern = Pattern.compile(regex);
    Resource root = resolver.getResource(path);
    List<ApmMap> values = StreamSupport.stream(root.getChildren().spliterator(), false)
        .filter(resource -> pattern.matcher(resource.getName()).matches())
        .map(resource -> {
          Map<String, ApmType> map = new HashMap<>();
          map.put("path", new ApmString(resource.getPath()));
          map.put("name", new ApmString(resource.getName()));
          return new ApmMap(map);
        })
        .collect(Collectors.toList());
    return new ApmList(values);
  }
}
