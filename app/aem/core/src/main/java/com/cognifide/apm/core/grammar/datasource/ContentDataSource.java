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

import com.cognifide.apm.core.grammar.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ContentDataSource implements DataSource {

  @Override
  public String getName() {
    return "CONTENT";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<ApmType> parameters) {
    List<Config> configs = determineConfigs(parameters);
    Resource root = resolver.getResource("/");
    return traverseTree(root, 0, configs);
  }

  private ApmType traverseTree(Resource root, int depth, List<Config> configs) {
    Config config = configs.get(depth);
    List<ApmType> list = new ArrayList<>();
    for (Resource resource : root.getChildren()) {
      Matcher matcher = config.pattern.matcher(resource.getPath());
      if (matcher.matches()) {
        ApmType items = new ApmEmpty();
        if (depth < configs.size() - 1) {
          items = traverseTree(resource, depth + 1, configs);
        }
        Map<String, ApmType> map = new HashMap<>();
        map.put("path", new ApmString(resource.getPath()));
        map.put("name", new ApmString(resource.getName()));
        for (int i = 0; i < config.paramNames.size(); i++) {
          map.put(config.paramNames.get(i), new ApmString(matcher.group(i + 1)));
        }
        if (depth < configs.size() - 1) {
          map.put("items", items);
        }
        if (items instanceof ApmEmpty || !CollectionUtils.isEmpty(items.getList())) {
          list.add(new ApmMap(map));
        }
      }
    }
    ApmType result;
    if (list.size() == 1 && config.paramNames.size() == 0) {
      result = list.get(0).getMap().get("items");
    } else {
      result = new ApmList(list);
    }
    return result;
  }

  private List<Config> determineConfigs(List<ApmType> parameters) {
    String regex = parameters.get(0).getString();
    List<ApmType> paramNames = parameters.get(1).getList();
    String[] parts = StringUtils.substringAfter(regex, "/").split("/");
    int paramIndex = 0;
    List<Config> configs = new ArrayList<>();
    for (String part : parts) {
      Config config = new Config();
      config.pattern = Pattern.compile(".*/" + part);
      config.paramNames = new ArrayList<>();
      for (int i = 0; i < StringUtils.countMatches(part, "("); i++) {
        config.paramNames.add(paramNames.get(paramIndex++).getString());
      }
      configs.add(config);
    }
    return configs;
  }

  private class Config {

    Pattern pattern;

    List<String> paramNames;
  }
}
