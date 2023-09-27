package com.cognifide.apm.core.grammar.datasource;

import com.cognifide.apm.core.grammar.ApmList;
import com.cognifide.apm.core.grammar.ApmString;
import com.cognifide.apm.core.grammar.ApmType;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class NodeNamesDataSource implements DataSource {

  @Override
  public String getName() {
    return "NODE_NAMES";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<ApmType> parameters) {
    String path = parameters.get(0).getString();
    String regex = parameters.size() >= 2 ? parameters.get(1).getString() : "[^:]+";
    Pattern pattern = Pattern.compile(regex);
    Resource resource = resolver.getResource(path);
    List<ApmString> values = StreamSupport.stream(resource.getChildren().spliterator(), false)
        .map(Resource::getName)
        .filter(name -> pattern.matcher(name).matches())
        .map(ApmString::new)
        .collect(Collectors.toList());
    return new ApmList(values);
  }
}
