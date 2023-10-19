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
package com.cognifide.apm.install.launchers;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.grammar.ReferenceFinder;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.history.HistoryEntry;
import com.cognifide.apm.core.launchers.AbstractLauncher;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.services.version.ScriptVersion;
import com.cognifide.apm.core.services.version.VersionService;
import com.cognifide.apm.core.utils.RuntimeUtils;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
    immediate = true,
    property = {
        Property.DESCRIPTION + "APM Launches configured scripts",
        Property.VENDOR
    }
)
@Designate(ocd = ApmInstallService.Configuration.class, factory = true)
public class ApmInstallService extends AbstractLauncher implements Runnable {

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

  private List<String> scriptPaths;

  private boolean ifModified;

  private Set<ServiceRegistration<ResourceChangeListener>> registrations;

  @Activate
  public void activate(Configuration config, BundleContext bundleContext) {
    scriptPaths = Arrays.asList(config.scriptPaths());
    ifModified = config.ifModified();
    processAllScripts();
    registerScripts(bundleContext);
  }

  @Deactivate
  public void deactivate() {
    registrations.forEach(ServiceRegistration::unregister);
    registrations.clear();
  }

  @Override
  public void run() {
    processAllScripts();
  }

  private void processAllScripts() {
    SlingHelper.operateTraced(resolverProvider, resolver -> {
      if (RuntimeUtils.isMutableContentInstance(resolver)) {
        List<Script> scripts = determineScripts(scriptPaths, resolver);
        processScripts(scripts, resolver);
      }
    });
  }

  private List<Script> determineScripts(List<String> scriptPaths, ResourceResolver resolver) {
    return scriptPaths.stream()
        .map(scriptPath -> scriptFinder.find(scriptPath, resolver))
        .filter(script -> isValid(script, resolver))
        .collect(Collectors.toList());
  }

  private boolean isValid(Script script, ResourceResolver resolver) {
    if (script == null) {
      return false;
    }
    if (!ifModified) {
      return true;
    }
    ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resolver);
    List<Script> subtree = referenceFinder.findReferences(script);
    String checksum = versionService.countChecksum(subtree);
    ScriptVersion scriptVersion = versionService.getScriptVersion(resolver, script);
    HistoryEntry lastLocalRun = history.findScriptHistory(resolver, script).getLastLocalRun();
    return !checksum.equals(scriptVersion.getLastChecksum())
        || lastLocalRun == null
        || !checksum.equals(lastLocalRun.getChecksum());
  }

  private void registerScripts(BundleContext bundleContext) {
    registrations = new HashSet<>();
    SlingHelper.operateTraced(resolverProvider, resolver -> {
      if (ifModified && RuntimeUtils.isMutableContentInstance(resolver)) {
        scriptPaths.forEach(scriptPath -> registerScript(scriptPath, resolver, bundleContext));
      }
    });
  }

  private void registerScript(String scriptPath, ResourceResolver resolver, BundleContext bundleContext) {
    Script script = scriptFinder.find(scriptPath, resolver);
    ScriptResourceChangeListener service = new ScriptResourceChangeListener(script, scriptPath);
    Dictionary<String, Object> dictionary = new Hashtable<>();
    dictionary.put(ResourceChangeListener.CHANGES, new String[]{"ADDED", "CHANGED", "REMOVED"});
    dictionary.put(ResourceChangeListener.PATHS, scriptPath);
    ServiceRegistration<ResourceChangeListener> registration = bundleContext.registerService(ResourceChangeListener.class, service, dictionary);
    registrations.add(registration);
  }

  @Override
  protected ScriptManager getScriptManager() {
    return scriptManager;
  }

  private class ScriptResourceChangeListener implements ResourceChangeListener {

    private Script script;

    private final String scriptPath;

    public ScriptResourceChangeListener(Script script, String scriptPath) {
      this.script = script;
      this.scriptPath = scriptPath;
    }

    @Override
    public void onChange(List<ResourceChange> changes) {
      SlingHelper.operateTraced(resolverProvider, resolver -> {
        Script newScript = scriptFinder.find(scriptPath, resolver);
        if (!Objects.equals(script, newScript)) {
          script = newScript;
          List<Script> scripts = determineScripts(Collections.singletonList(scriptPath), resolver);
          processScripts(scripts, resolver);
        }
      });
    }
  }

  @ObjectClassDefinition(name = "AEM Permission Management - Install Launcher Configuration")
  public @interface Configuration {

    @AttributeDefinition(name = "Script Paths")
    String[] scriptPaths();

    @AttributeDefinition(name = "If Modified", description = "Executed script, only if script content's changed")
    boolean ifModified();

    @AttributeDefinition(name = "CRON Expression", description = "Cron expression for scheduled execution")
    String scheduler_expression();
  }
}
