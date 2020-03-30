/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.cq.cqsm.core.scripts;

import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.core.Property;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.jcr.query.Query;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(
    immediate = true,
    service = ScriptFinder.class,
    property = {
        ScriptFinderImpl.SEARCH_PATHS + "=" + ScriptFinderImpl.SCRIPT_PATH,
        Property.DESCRIPTION + "CQSM Script Finder Service",
        Property.VENDOR
    }
)
public class ScriptFinderImpl implements ScriptFinder {

  private static final String QUERY = "SELECT * FROM [nt:resource] WHERE ISDESCENDANTNODE([%s]) AND [jcr:mixinTypes] = 'cqsm:File'";

  private static final String ROOT_PATH = "/conf/apm";

  static final String SCRIPT_PATH = ROOT_PATH + "/scripts";

  static final String SEARCH_PATHS = "search.paths";

  private String[] searchPaths = new String[]{};

  @Activate
  protected void activate(final ComponentContext componentContext) {
    this.searchPaths = PropertiesUtil.toStringArray(componentContext.getProperties().get(SEARCH_PATHS), new String[]{});
  }

  @Override
  public List<Script> findAll(Predicate<Script> filter, ResourceResolver resolver) {
    final List<Script> scripts = findAll(resolver).stream()
        .filter(filter)
        .collect(Collectors.toList());
    return scripts;
  }

  @Override
  public List<Script> findAll(ResourceResolver resolver) {
    return findScripts(resolver)
        .map(resource -> resource.adaptTo(ScriptImpl.class))
        .collect(Collectors.toList());
  }

  @Override
  public Script find(String path, ResourceResolver resolver) {
    return find(path, true, resolver);
  }
  @Override
  public Script find(String scriptPath, boolean skipIgnored, ResourceResolver resolver) {
    Script result = null;
    if (StringUtils.isNotEmpty(scriptPath) && (!skipIgnored || isNotIgnoredPath(scriptPath))) {
      Resource resource;
      if (isAbsolute(scriptPath)) {
        resource = resolver.getResource(scriptPath);
      } else {
        resource = Stream.of(searchPaths)
            .map(searchPath -> searchPath + "/" + scriptPath)
            .map(path -> resolver.getResource(path))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
      }
      if (resource != null) {
        result = resource.adaptTo(ScriptImpl.class);
      }
    }
    return result;
  }

  private Stream<Resource> findScripts(ResourceResolver resolver) {
    return Stream.of(searchPaths)
        .map(searchPath -> String.format(QUERY, searchPath))
        .map(query -> resolver.findResources(query, Query.JCR_SQL2))
        .map(resourceIterator -> Spliterators.spliteratorUnknownSize(resourceIterator, Spliterator.ORDERED))
        .flatMap(resourceSpliterator -> StreamSupport.stream(resourceSpliterator, false))
        .map(resource -> resource.getParent())
        .filter(Objects::nonNull);
  }

  private boolean isNotIgnoredPath(String path) {
    return !ScriptManager.FILE_FOR_EVALUATION.equals(FilenameUtils.getBaseName(path));
  }

  private boolean isAbsolute(String path) {
    return StringUtils.startsWith(path, "/");
  }
}
