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
import java.util.Collections;
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
public class LevelsDataSource implements DataSource {

  @Override
  public String getName() {
    return "LEVELS";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<Object> parameters) {
    String rootPath = (String) parameters.get(0);
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
            "name", new ApmString(resource.getName())
        ));
        map.putAll(config.determineParams(resource));
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

  private List<Config> determineConfigs(List<Object> parameters) {
    return ((List<Object>) parameters.get(1))
        .stream()
        .map(item -> (Map<String, Object>) item)
        .map(Config::new)
        .collect(Collectors.toList());
  }

  private static class Config {

    private final Pattern pattern;

    private final Pattern excludePattern;

    private final String template;

    private final String resourceType;

    private final List<String> paramNames;

    private final List<ConfigProperty> properties;

    public Config(Map<String, Object> map) {
      String regex = (String) map.get("excludeRegex");
      pattern = StringUtils.isNotEmpty(regex) ? Pattern.compile(regex) : null;
      String excludeRegex = (String) map.get("excludeRegex");
      excludePattern = StringUtils.isNotEmpty(excludeRegex) ? Pattern.compile(excludeRegex) : null;
      template = (String) map.get("template");
      resourceType = (String) map.get("resourceType");
      paramNames = (List<String>) map.getOrDefault("paramNames", Collections.emptyList());
      if (StringUtils.countMatches(regex, "(") != paramNames.size()) {
        throw new IllegalArgumentException("Number of paramNames must match number of regex groups");
      }
      properties = ((List<Object>) map.getOrDefault("properties", Collections.emptyList()))
          .stream()
          .map(it -> (Map<String, Object>) it)
          .map(ConfigProperty::new)
          .collect(Collectors.toList());
    }

    public boolean isValid(Resource resource) {
      boolean result = true;
      if (pattern != null) {
        result = pattern.matcher(resource.getName()).matches();
      }
      if (result && excludePattern != null) {
        result = !excludePattern.matcher(resource.getName()).matches();
      }
      if (result && !StringUtils.isAllEmpty(template, resourceType)) {
        ValueMap valueMap;
        if (resource.isResourceType(NameConstants.NT_PAGE)) {
          valueMap = Optional.ofNullable(resource.getChild(JcrConstants.JCR_CONTENT))
              .map(Resource::getValueMap)
              .orElse(ValueMap.EMPTY);
        } else if (!resource.isResourceType("cq:PageContent")) {
          valueMap = resource.getValueMap();
        } else {
          valueMap = ValueMap.EMPTY;
        }
        result = StringUtils.isEmpty(template) || StringUtils.equals(template, valueMap.get(NameConstants.PN_TEMPLATE, String.class));
        result &= StringUtils.isEmpty(resourceType) || StringUtils.equals(resourceType, valueMap.get(ResourceResolver.PROPERTY_RESOURCE_TYPE, String.class));
      }
      if (result && CollectionUtils.isNotEmpty(properties)) {
        result = properties.stream()
            .allMatch(property -> property.isValid(resource));
      }
      return result;
    }

    public Map<String, ApmType> determineParams(Resource resource) {
      Map<String, ApmType> params = new HashMap<>();
      if (pattern != null && paramNames.size() > 0) {
        Matcher matcher = pattern.matcher(resource.getName());
        for (int i = 0; i < paramNames.size(); i++) {
          params.put(paramNames.get(i), new ApmString(matcher.group(i + 1)));
        }
      }
      return params;
    }
  }

  private static class ConfigProperty {

    private final String name;

    private final Pattern pattern;

    private final Pattern excludePattern;

    public ConfigProperty(Map<String, Object> map) {
      name = (String) map.get("name");
      String regex = (String) map.get("excludeRegex");
      pattern = StringUtils.isNotEmpty(regex) ? Pattern.compile(regex) : null;
      String excludeRegex = (String) map.get("excludeRegex");
      excludePattern = StringUtils.isNotEmpty(excludeRegex) ? Pattern.compile(excludeRegex) : null;
    }

    public boolean isValid(Resource resource) {
      String value = resource.getValueMap().get(name, String.class);
      boolean result = StringUtils.isNotEmpty(value);
      if (result && pattern != null) {
        result = pattern.matcher(resource.getName()).matches();
      }
      if (result && excludePattern != null) {
        result = !excludePattern.matcher(resource.getName()).matches();
      }
      return result;
    }
  }
}
