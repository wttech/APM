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

import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.ApmType.ApmList;
import com.cognifide.apm.core.grammar.ApmType.ApmMap;
import com.cognifide.apm.core.grammar.ApmType.ApmString;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component
public class NodesDataSource implements DataSource {

  @Override
  public String getName() {
    return "NODES";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<Object> parameters) {
    String path = (String) parameters.get(0);
    String regex = (String) parameters.get(1);
    Pattern pattern = Pattern.compile(regex);
    Resource root = resolver.getResource(path);
    List<ApmType> values = StreamSupport.stream(root.getChildren().spliterator(), false)
        .filter(resource -> pattern.matcher(resource.getName()).matches())
        .map(resource -> new ApmMap(ImmutableMap.of(
            "path", new ApmString(resource.getPath()),
            "name", new ApmString(resource.getName())
        )))
        .collect(Collectors.toList());
    return new ApmList(values);
  }
}
