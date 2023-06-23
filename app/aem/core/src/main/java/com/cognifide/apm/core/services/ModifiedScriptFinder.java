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
package com.cognifide.apm.core.services;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.grammar.ReferenceFinder;
import com.cognifide.apm.core.grammar.ScriptExecutionException;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.history.ScriptHistory;
import com.cognifide.apm.core.services.version.ScriptVersion;
import com.cognifide.apm.core.services.version.VersionService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = ModifiedScriptFinder.class,
    property = {
        Property.VENDOR
    }
)
public class ModifiedScriptFinder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModifiedScriptFinder.class);

  @Reference
  private VersionService versionService;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private History history;

  public List<Script> findAll(Predicate<Script> filter, ResourceResolver resolver) {
    List<Script> all = scriptFinder.findAll(filter, resolver);
    ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resolver);
    List<Script> modified = new ArrayList<>();

    all.stream()
        .filter(Script::isValid)
        .forEach(script -> {
          try {
            List<Script> subtree = referenceFinder.findReferences(script);
            String checksum = versionService.countChecksum(subtree);
            ScriptVersion scriptVersion = versionService.getScriptVersion(resolver, script);
            ScriptHistory scriptHistory = history.findScriptHistory(resolver, script);
            if (!StringUtils.equals(checksum, scriptVersion.getLastChecksum())
                || scriptHistory.getLastLocalRun() == null
                || !StringUtils.equals(checksum, scriptHistory.getLastLocalRun().getChecksum())) {
              modified.add(script);
            }
          } catch (ScriptExecutionException e) {
            LOGGER.error(e.getMessage());
          }
        });
    return modified;
  }
}
