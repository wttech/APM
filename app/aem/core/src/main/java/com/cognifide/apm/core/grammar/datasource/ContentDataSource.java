package com.cognifide.apm.core.grammar.datasource;

import com.cognifide.apm.core.grammar.ApmList;
import com.cognifide.apm.core.grammar.ApmMap;
import com.cognifide.apm.core.grammar.ApmString;
import com.cognifide.apm.core.grammar.ApmType;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    Resource root = resolver.getResource("/content");
    return traverseTree(root, 0, configs);
  }

  private ApmType traverseTree(Resource root, int depth, List<Config> configs) {
    Config config = configs.get(depth);
    List<ApmType> list = new ArrayList<>();
    for (Resource resource : root.getChildren()) {
      Matcher matcher = config.pattern.matcher(resource.getPath());
      if (matcher.matches()) {
        Map<String, ApmType> map = new HashMap<>();
        map.put("path", new ApmString(resource.getPath()));
        map.put("name", new ApmString(resource.getName()));
        for (int i = 0; i < config.paramNames.size(); i++) {
          map.put(config.paramNames.get(i), new ApmString(matcher.group(i + 1).toLowerCase()));
        }
        if (depth < configs.size() - 1) {
          map.put("pages", traverseTree(resource, depth + 1, configs));
        }
        list.add(new ApmMap(map));
      }
    }
    return new ApmList(list);
  }

  private List<Config> determineConfigs(List<ApmType> parameters) {
    String regex = parameters.get(0).getString();
    List<ApmType> paramNames = parameters.get(1).getList();
    String[] parts = StringUtils.substringAfter(regex, "/content/").split("/");
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
