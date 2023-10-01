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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceDecorator;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = {ScriptRootPathsProvider.class, ResourceDecorator.class}, immediate = true)
@Designate(ocd = ScriptRootPathsProvider.Configuration.class)
public class ScriptRootPathsProvider implements ResourceDecorator {

  private static final String RESOURCE_TYPE = "wcm/commons/ui/shell/datasources/breadcrumbs";

  private static final String RESOURCE_PATH = "/apps/apm/views/scripts/jcr:content/breadcrumbs";

  private static final String DEFAULT_SCRIPT_PATH = "/conf/apm/scripts";

  private Set<String> rootPaths;

  @Activate
  public void activate(Configuration config) {
    this.rootPaths = new HashSet<>();
    this.rootPaths.add(DEFAULT_SCRIPT_PATH);
    this.rootPaths.addAll(Arrays.asList(config.rootPaths()));
  }

  @Override
  public Resource decorate(Resource resource) {
    Resource result = resource;
    if (isAllowed(resource)) {
      ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
      valueMap.putAll(resource.getValueMap());
      valueMap.put("rootPath", "/");
      result = new ResourceWrapper(resource) {
        @Override
        public ValueMap getValueMap() {
          return valueMap;
        }
      };
    }
    return result;
  }

  @Override
  @SuppressWarnings("deprecation")
  public Resource decorate(Resource resource, HttpServletRequest request) {
    return decorate(resource);
  }

  public boolean isValidPath(String path) {
    return rootPaths.stream()
        .anyMatch(rootPath -> path.startsWith(rootPath) || rootPath.startsWith(path));
  }

  private boolean isAllowed(Resource resource) {
    return StringUtils.equals(resource.getResourceType(), RESOURCE_TYPE)
        && StringUtils.equals(resource.getPath(), RESOURCE_PATH)
        && rootPaths.size() > 1;
  }

  @ObjectClassDefinition(name = "AEM Permission Management - Script Root Paths Provider")
  public @interface Configuration {

    @AttributeDefinition(name = "Additional Script Root Paths")
    String[] rootPaths();
  }
}
