package com.cognifide.apm.core.services;

import com.cognifide.apm.api.services.DefinitionsProvider;
import com.cognifide.apm.core.Property;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
    immediate = true,
    property = {
        Property.DESCRIPTION + "APM Custom Definitions Provider",
        Property.VENDOR
    }
)
@Designate(ocd = CustomDefinitionsProvider.Configuration.class, factory = true)
public class CustomDefinitionsProvider implements DefinitionsProvider {

  private Map<String, String> predefinedDefinitions;

  @Activate
  public void activate(Configuration config) {
    predefinedDefinitions = Arrays.stream(config.definitions())
        .map(definition -> definition.split("="))
        .map(parts -> StringUtils.stripAll(parts))
        .filter(parts -> StringUtils.isNoneEmpty(parts))
        .filter(parts -> parts.length == 2)
        .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1], (a, b) -> b);
  }

  @Override
  public Map<String, String> getPredefinedDefinitions() {
    return predefinedDefinitions;
  }

  @ObjectClassDefinition(name = "AEM Permission Management - Custom Definitions Configuration")
  public @interface Configuration {

    @AttributeDefinition(name = "Definitions", description = "format: key=value")
    String[] definitions();
  }
}
