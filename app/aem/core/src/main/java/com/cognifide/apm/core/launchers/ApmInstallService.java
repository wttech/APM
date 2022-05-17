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
package com.cognifide.apm.core.launchers;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.grammar.ReferenceFinder;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.history.HistoryEntry;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.services.version.ScriptVersion;
import com.cognifide.apm.core.services.version.VersionService;
import com.cognifide.apm.core.utils.RuntimeUtils;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
    immediate = true,
    service = ApmInstallService.class,
    property = {
        Property.DESCRIPTION + "APM Launches configured scripts",
        Property.VENDOR,
        Property.SCHEDULER + "0 * * * * ?"
    }
)
@Designate(ocd = ApmInstallService.Configuration.class, factory = true)
public class ApmInstallService extends AbstractLauncher {

  @Reference
  private ResourceResolverProvider resolverProvider;

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private VersionService versionService;

  @Reference
  private History history;

  @Activate
  public void activate(Configuration config) {
    SlingHelper.operateTraced(resolverProvider, resolver -> processScripts(config, resolver));
  }

  private void processScripts(Configuration config, ResourceResolver resolver) throws PersistenceException {
    logger.info("scriptPaths = {}", Arrays.asList(config.scriptPaths()));
    logger.info("ifModified = {}", config.ifModified());
    ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resolver);
    boolean compositeNodeStore = RuntimeUtils.determineCompositeNodeStore(resolver);
    logger.info("compositeNodeStore = {}", compositeNodeStore);
    List<Script> scripts = Arrays.stream(config.scriptPaths())
        .map(scriptPath -> {
          logger.info("scriptPath = {}", scriptPath);
          Script script = scriptFinder.find(scriptPath, resolver);
          logger.info("scriptPath = {}  script.exists = {}", scriptPath, script != null);
          return script;
        })
        .filter(Objects::nonNull)
        .filter(script -> {
          List<Script> subtree = referenceFinder.findReferences(script);
          String checksum = versionService.countChecksum(subtree);
          ScriptVersion scriptVersion = versionService.getScriptVersion(resolver, script);
          HistoryEntry lastLocalRun = history.findScriptHistory(resolver, script).getLastLocalRun();
          logger.info("script.path = {}  checksum = {}", script.getPath(), checksum);
          if (scriptVersion.getLastChecksum() == null) {
            logger.info("script.path = {}  scriptVersion.lastChecksum = null", script.getPath());
          } else {
            logger.info("script.path = {}  scriptVersion.lastChecksum = {}", script.getPath(), scriptVersion.getLastChecksum());
          }
          if (lastLocalRun == null) {
            logger.info("script.path = {}  lastLocalRun = null", script.getPath());
          } else {
            logger.info("script.path = {}  lastLocalRun.checksum = {}", script.getPath(), lastLocalRun.getChecksum());
            logger.info("script.path = {}  lastLocalRun.compositeNodeStore = {}", script.getPath(), lastLocalRun.isCompositeNodeStore());
          }
          boolean result = !config.ifModified()
              || !checksum.equals(scriptVersion.getLastChecksum())
              || lastLocalRun == null
              || !checksum.equals(lastLocalRun.getChecksum())
              || compositeNodeStore != lastLocalRun.isCompositeNodeStore();
          logger.info("script.path = {}  result = {}", script.getPath(), result);
          return result;
        })
        .collect(Collectors.toList());
    logger.info("scripts.size = {}", scripts.size());
    processScripts(scripts, resolver);
  }

  @Override
  protected ScriptManager getScriptManager() {
    return scriptManager;
  }

  @ObjectClassDefinition(name = "AEM Permission Management - Install Launcher Configuration")
  public @interface Configuration {

    @AttributeDefinition(name = "Scripts Path")
    String[] scriptPaths();

    @AttributeDefinition(name = "If Modified", description = "Executed script, only if script content's changed")
    boolean ifModified();

  }

}
