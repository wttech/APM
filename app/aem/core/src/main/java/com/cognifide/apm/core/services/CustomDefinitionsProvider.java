/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
        .map(StringUtils::stripAll)
        .filter(StringUtils::isNoneEmpty)
        .filter(parts -> parts.length == 2)
        .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1], (first, second) -> first));
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
