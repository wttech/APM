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

import static com.cognifide.apm.core.scripts.ScriptFilters.onStartup;
import static com.cognifide.apm.core.scripts.ScriptFilters.onStartupIfModified;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.services.ModifiedScriptFinder;
import com.cognifide.apm.core.services.version.VersionService;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = StartupScriptLauncher.class,
    property = {
        Property.DESCRIPTION + "Launches scripts on bundle startup",
        Property.VENDOR
    }
)
public class StartupScriptLauncher extends AbstractLauncher {

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private ModifiedScriptFinder modifiedScriptFinder;

  @Reference
  private SlingSettingsService slingSettings;

  @Reference
  private VersionService versionService;

  @Reference
  private ResourceResolverFactory resolverFactory;

  public void process() {
    SlingHelper.operateTraced(resolverFactory, this::process);
  }

  private void process(ResourceResolver resolver) {
    executeScripts(resolver);
  }

  private void executeScripts(ResourceResolver resolver) {
    List<Script> scripts = new ArrayList<>();
    scripts.addAll(scriptFinder.findAll(onStartup(slingSettings), resolver));
    scripts.addAll(modifiedScriptFinder.findAll(onStartupIfModified(slingSettings), resolver));

    scripts.forEach(script -> {
      try {
        ExecutionResult result = scriptManager.process(script, ExecutionMode.AUTOMATIC_RUN, resolver);
        logStatus(script.getPath(), result.isSuccess());
      } catch (RepositoryException | PersistenceException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void logStatus(String scriptPath, Boolean success) {
    if (success) {
      logger.info(String.format("Script successfully executed: %s", scriptPath));
    } else {
      throw new RuntimeException(String.format("Script cannot be executed properly: %s", scriptPath));
    }
  }

  @Override
  public ScriptManager getScriptManager() {
    return scriptManager;
  }

}
