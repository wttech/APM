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

package com.cognifide.apm.core.ui.datasources;

import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.ValueMapDecorator;

@RequiredArgsConstructor
class SimpleDataSourceBuilder {

  private static final String CONFIGURATION_NAME_PROP = "text";
  private static final String CONFIGURATION_PATH_PROP = "value";

  private final ResourceResolver resourceResolver;
  private final List<Option> options = new ArrayList<>();

  public SimpleDataSourceBuilder addOption(Option option) {
    options.add(option);
    return this;
  }

  public SimpleDataSourceBuilder addOption(Object name, Object value) {
    return addOption(name.toString(), value.toString());
  }

  public SimpleDataSourceBuilder addOption(String name, String value) {
    options.add(new Option(name, value));
    return this;
  }

  public SimpleDataSourceBuilder addOptions(Collection<Option> options) {
    options.addAll(options);
    return this;
  }

  public SimpleDataSource build() {
    List<Resource> resources = options.stream()
        .map(option -> createDataSourceItem(resourceResolver, option.name, option.value))
        .collect(Collectors.toList());
    return new SimpleDataSource(resources.iterator());
  }

  private Resource createDataSourceItem(ResourceResolver resolver, String name, String value) {
    Map<String, Object> valueMap = ImmutableMap.of(
        CONFIGURATION_NAME_PROP, name,
        CONFIGURATION_PATH_PROP, value
    );
    ValueMapDecorator result = new ValueMapDecorator(valueMap);
    return new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_RESOURCE, result);
  }

  @RequiredArgsConstructor
  public static class Option {

    private final String name;
    private final String value;
  }
}
