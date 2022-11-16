package com.cognifide.apm.core.method;

import com.cognifide.apm.core.grammar.ApmType;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolver;

public interface MethodInvoker {

  ApmType runMethod(ResourceResolver resourceResolver, String commandName, List<ApmType> arguments);
}
