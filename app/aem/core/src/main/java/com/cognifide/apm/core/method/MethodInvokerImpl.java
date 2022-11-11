package com.cognifide.apm.core.method;

import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.MethodInvoker;
import com.cognifide.apm.core.grammar.argument.Arguments;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component
public class MethodInvokerImpl implements MethodInvoker {

  private Map<String, MethodDefinition> methodDefinitions = new HashMap<>();

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, service = MethodDefinition.class)
  protected final void bindMethodDefinition(MethodDefinition methodDefinition) {
    methodDefinitions.put(methodDefinition.getName(), methodDefinition);
  }

  protected final void unbindMethodDefinition(MethodDefinition methodDefinition) {
    methodDefinitions.remove(methodDefinition.getName());
  }

  @Override
  public ApmType runMethod(ResourceResolver resourceResolver, String commandName, Arguments arguments) {
    return methodDefinitions.get(commandName).runMethod(resourceResolver, arguments);
  }
}
