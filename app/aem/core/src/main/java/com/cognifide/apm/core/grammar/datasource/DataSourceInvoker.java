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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = DataSourceInvoker.class)
public class DataSourceInvoker {

  private final Map<String, DataSource> dataSources = new HashMap<>();

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, service = DataSource.class)
  protected final void bindDataSource(DataSource dataSource) {
    dataSources.put(dataSource.getName().toLowerCase(), dataSource);
  }

  protected final void unbindDataSource(DataSource dataSource) {
    dataSources.remove(dataSource.getName().toLowerCase());
  }

  public ApmType determine(String name, ResourceResolver resolver, List<ApmType> parameters) {
    DataSource dataSource = dataSources.get(name.toLowerCase());
    return dataSource == null ? null : dataSource.determine(resolver, parameters);
  }
}
