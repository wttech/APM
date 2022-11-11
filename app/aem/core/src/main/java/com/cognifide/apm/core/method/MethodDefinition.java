package com.cognifide.apm.core.method;

import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.argument.Arguments;
import org.apache.sling.api.resource.ResourceResolver;

public interface MethodDefinition {

  String getName();

  ApmType runMethod(ResourceResolver resourceResolver, Arguments arguments);
}
