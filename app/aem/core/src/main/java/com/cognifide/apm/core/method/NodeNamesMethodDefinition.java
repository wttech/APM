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
