package com.cognifide.apm.core.method;

import com.cognifide.apm.core.grammar.ApmList;
import com.cognifide.apm.core.grammar.ApmString;
import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.argument.Arguments;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component
public class NodeNamesMethodDefinition implements MethodDefinition {

  @Override
  public String getName() {
    return "NODE_NAMES";
  }

  @Override
  public ApmType runMethod(ResourceResolver resourceResolver, Arguments arguments) {
    String path = arguments.getRequired().get(0).getString();
    Resource resource = resourceResolver.getResource(path);
    List<ApmString> values = StreamSupport.stream(resource.getChildren().spliterator(), false)
        .filter(this::isValidResource)
        .map(Resource::getName)
        .map(ApmString::new)
        .collect(Collectors.toList());
    return new ApmList(values);
  }

  private boolean isValidResource(Resource resource) {
    return !resource.getName().contains(":");
  }
}
