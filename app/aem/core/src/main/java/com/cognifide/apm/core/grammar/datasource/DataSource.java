package com.cognifide.apm.core.grammar.datasource;

import com.cognifide.apm.core.grammar.ApmEmpty;
import com.cognifide.apm.core.grammar.ApmType;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface DataSource {

  String getName();

  ApmType determine(ResourceResolver resolver, List<ApmType> parameters);
}
