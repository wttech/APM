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

import com.cognifide.apm.core.grammar.ApmEmpty;
import com.cognifide.apm.core.grammar.ApmList;
import com.cognifide.apm.core.grammar.ApmMap;
import com.cognifide.apm.core.grammar.ApmString;
import com.cognifide.apm.core.grammar.ApmType;
import com.day.cq.wcm.api.NameConstants;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

@Component
public class TraverseDataSource implements DataSource {

  @Override
  public String getName() {
    return "TRAVERSE";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<ApmType> parameters) {
    String rootPath = parameters.get(0).getString();
    List<Config> configs = determineConfigs(parameters);
    Resource root = resolver.getResource(rootPath);
    return traverseTree(root, 0, configs);
  }

  private ApmType traverseTree(Resource root, int depth, List<Config> configs) {
    if (depth == configs.size()) {
      return new ApmEmpty();
    }
    Config config = configs.get(depth);
    List<ApmType> list = new ArrayList<>();
    for (Resource resource : root.getChildren()) {
      if (config.isValid(resource)) {
        Map<String, ApmType> map = new HashMap<>(ImmutableMap.of(
            "path", new ApmString(resource.getPath()),
            "name", new ApmString(resource.getName()))
        );
        if (config.pattern != null && config.paramNames.size() > 0) {
          Matcher matcher = config.pattern.matcher(resource.getName());
          for (int i = 0; i < config.paramNames.size(); i++) {
            map.put(config.paramNames.get(i), new ApmString(matcher.group(i + 1)));
          }
        }
        ApmType items = traverseTree(resource, depth + 1, configs);
        if (items instanceof ApmList) {
          map.put("items", items);
        }
        if (items instanceof ApmEmpty || !CollectionUtils.isEmpty(items.getList())) {
          list.add(new ApmMap(map));
        }
      }
    }
    return new ApmList(list);
  }

  private List<Config> determineConfigs(List<ApmType> parameters) {
    return parameters.get(1)
        .getList()
        .stream()
        .map(ApmType::getMap)
        .map(Config::new)
        .collect(Collectors.toList());
  }

  private static class Config {

    Pattern pattern;

    Pattern excludePattern;

    String template;

    String resourceType;

    List<String> paramNames;

    Config(Map<String, ApmType> map) {
      String regex = map.getOrDefault("regex", new ApmEmpty()).getString();
      if (StringUtils.isNotEmpty(regex)) {
        pattern = Pattern.compile(regex);
      }
      String excludeRegex = map.getOrDefault("excludeRegex", new ApmEmpty()).getString();
      if (StringUtils.isNotEmpty(excludeRegex)) {
        excludePattern = Pattern.compile(excludeRegex);
      }
      template = map.getOrDefault("template", new ApmEmpty()).getString();
      resourceType = map.getOrDefault("resourceType", new ApmEmpty()).getString();
      paramNames = map.getOrDefault("paramNames", new ApmList(new ArrayList<>()))
          .getList()
          .stream()
          .map(ApmType::getString)
          .collect(Collectors.toList());
      if (StringUtils.countMatches(regex, "(") != paramNames.size()) {
        throw new IllegalArgumentException("Number of paramNames must match number of regex groups");
      }
    }

    public boolean isValid(Resource resource) {
      boolean result = true;
      if (pattern != null) {
        result &= pattern.matcher(resource.getName()).matches();
      }
      if (excludePattern != null) {
        result &= !excludePattern.matcher(resource.getName()).matches();
      }
      if (!StringUtils.isAllEmpty(template, resourceType)) {
        result &= resource.isResourceType(NameConstants.NT_PAGE);
        ValueMap valueMap = Optional.ofNullable(resource.getChild(JcrConstants.JCR_CONTENT))
            .map(Resource::getValueMap)
            .orElse(ValueMap.EMPTY);
        result &= StringUtils.isEmpty(template) || StringUtils.equals(template, valueMap.get(NameConstants.PN_TEMPLATE, String.class));
        result &= StringUtils.isEmpty(resourceType) || StringUtils.equals(resourceType, valueMap.get(ResourceResolver.PROPERTY_RESOURCE_TYPE, String.class));
      }
      return result;
    }
  }
}