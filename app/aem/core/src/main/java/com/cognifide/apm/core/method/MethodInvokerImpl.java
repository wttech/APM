/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2018 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
