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
package com.cognifide.apm.core.scripts;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.AbstractResourceVisitor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component(
    property = {
        Property.DESCRIPTION + "APM Script Finder Service",
        Property.VENDOR
    }
)
public class ScriptFinderImpl implements ScriptFinder {

  private static final String SCRIPT_PATH = "/conf/apm/scripts";

  @Override
  public List<Script> findAll(Predicate<Script> filter, ResourceResolver resolver) {
    List<Script> scripts = new LinkedList<>();
    AbstractResourceVisitor visitor = new AbstractResourceVisitor() {
      @Override
      protected void visit(Resource resource) {
        if (ScriptModel.isScript(resource)) {
          Script script = resource.adaptTo(ScriptModel.class);
          if (filter.test(script)) {
            scripts.add(script);
          }
        }
      }
    };
    Resource rootResource = resolver.getResource(SCRIPT_PATH);
    visitor.accept(rootResource);
    return scripts;
  }

  @Override
  public List<Script> findAll(ResourceResolver resolver) {
    return findAll(filter -> true, resolver);
  }

  @Override
  public Script find(String scriptPath, ResourceResolver resolver) {
    Script result = null;
    if (StringUtils.isNotEmpty(scriptPath)) {
      Resource resource;
      if (isAbsolute(scriptPath)) {
        resource = resolver.getResource(scriptPath);
      } else {
        resource = resolver.getResource(SCRIPT_PATH + "/" + scriptPath);
      }
      if (resource != null && ScriptModel.isScript(resource)) {
        result = resource.adaptTo(ScriptModel.class);
      }
    }
    return result;
  }

  private boolean isAbsolute(String path) {
    return StringUtils.startsWith(path, "/");
  }
}
