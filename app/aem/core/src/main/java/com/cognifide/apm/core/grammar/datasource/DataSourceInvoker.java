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

import com.cognifide.apm.core.crypto.DecryptionService;
import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.argument.ArgumentResolverException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = DataSourceInvoker.class)
public class DataSourceInvoker {

  @Reference
  private DecryptionService decryptionService;

  private final Map<String, DataSource> dataSources = new HashMap<>();

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, service = DataSource.class)
  protected final void bindDataSource(DataSource dataSource) {
    dataSources.put(dataSource.getName().toUpperCase(), dataSource);
  }

  protected final void unbindDataSource(DataSource dataSource) {
    dataSources.remove(dataSource.getName().toUpperCase());
  }

  public ApmType determine(String name, ResourceResolver resolver, List<ApmType> parameters) {
    DataSource dataSource = dataSources.get(name.toUpperCase());
    try {
      List<Object> decryptedParameters = parameters.stream()
          .map(parameter -> parameter.getArgument(decryptionService))
          .collect(Collectors.toList());
      return dataSource == null ? null : dataSource.determine(resolver, decryptedParameters);
    } catch (Exception e) {
      throw new ArgumentResolverException(String.format("%s data source: %s", name.toUpperCase(), e.getMessage()));
    }
  }
}
