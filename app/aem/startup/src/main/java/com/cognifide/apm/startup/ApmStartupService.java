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
package com.cognifide.apm.startup;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.grammar.ReferenceFinder;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.history.HistoryEntry;
import com.cognifide.apm.core.history.HistoryImpl;
import com.cognifide.apm.core.launchers.AbstractLauncher;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.services.version.ScriptVersion;
import com.cognifide.apm.core.services.version.VersionService;
import com.cognifide.apm.core.services.version.VersionServiceImpl;
import com.cognifide.apm.core.utils.RuntimeUtils;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
    property = {
        Property.DESCRIPTION + "APM Launches configured scripts on startup",
        Property.VENDOR
    }
)
@Designate(ocd = ApmStartupService.Configuration.class)
public class ApmStartupService extends AbstractLauncher {

  private static final String HISTORY_APPS_FOLDER = "/apps/apm/history";

  private static final String VERSIONS_APPS_FOLDER = "/apps/apm/versions";

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverProvider resolverProvider;

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private ScriptManager scriptManager;

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private ScriptFinder scriptFinder;

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private VersionService versionService;

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private History history;

  @Activate
  public void activate(Configuration config) {
    SlingHelper.operateTraced(resolverProvider, resolver -> processScripts(config, resolver));
  }

  private void processScripts(Configuration config, ResourceResolver resolver) throws PersistenceException, RepositoryException {
    ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resolver);
    boolean compositeNodeStore = RuntimeUtils.determineCompositeNodeStore(resolver);
    List<Script> scripts = Arrays.stream(config.scriptPaths())
        .map(scriptPath -> scriptFinder.find(scriptPath, resolver))
        .filter(Objects::nonNull)
        .filter(script -> {
          List<Script> subtree = referenceFinder.findReferences(script);
          String checksum = versionService.countChecksum(subtree);
          ScriptVersion scriptVersion = versionService.getScriptVersion(resolver, script);
          HistoryEntry lastLocalRun = history.findScriptHistory(resolver, script).getLastLocalRun();
          return !config.ifModified()
              || !checksum.equals(scriptVersion.getLastChecksum())
              || lastLocalRun == null
              || !checksum.equals(lastLocalRun.getChecksum())
              || compositeNodeStore != lastLocalRun.isCompositeNodeStore();
        })
        .collect(Collectors.toList());
    processScripts(scripts, resolver);
    if (!compositeNodeStore) {
      copyHistoryToApps(resolver);
    }
  }

  private void copyHistoryToApps(ResourceResolver resolver) throws RepositoryException {
    Session session = resolver.adaptTo(Session.class);
    if (session.nodeExists(HistoryImpl.HISTORY_FOLDER) && !session.nodeExists(HISTORY_APPS_FOLDER)) {
      session.getWorkspace().copy(HistoryImpl.HISTORY_FOLDER, HISTORY_APPS_FOLDER);
    }
    if (session.nodeExists(VersionServiceImpl.versionsRoot) && !session.nodeExists(VERSIONS_APPS_FOLDER)) {
      session.getWorkspace().copy(VersionServiceImpl.versionsRoot, VERSIONS_APPS_FOLDER);
    }
    session.save();
  }

  @Override
  protected ScriptManager getScriptManager() {
    return scriptManager;
  }

  @ObjectClassDefinition(name = "AEM Permission Management - Startup Launcher Configuration")
  public @interface Configuration {

    @AttributeDefinition(name = "Scripts Path")
    String[] scriptPaths();

    @AttributeDefinition(name = "If Modified", description = "Executed script, only if script content's changed")
    boolean ifModified();

  }

}
