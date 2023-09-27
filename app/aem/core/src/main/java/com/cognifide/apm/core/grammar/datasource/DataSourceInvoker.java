package com.cognifide.apm.core.grammar.datasource;

import com.cognifide.apm.core.grammar.ApmType;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
