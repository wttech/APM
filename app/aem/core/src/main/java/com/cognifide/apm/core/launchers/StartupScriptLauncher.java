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

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.services.ModifiedScriptFinder;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.ArrayList;
import java.util.List;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = StartupScriptLauncher.class,
    property = {
        Property.DESCRIPTION + "APM Launches scripts on bundle startup",
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
  private ResourceResolverProvider resolverProvider;

  @Activate
  public void activate() {
    SlingHelper.operateTraced(resolverProvider, this::executeScripts);
  }

  private void executeScripts(ResourceResolver resolver) throws PersistenceException {
    LaunchEnvironment environment = LaunchEnvironment.of(slingSettings);
    List<Script> scripts = new ArrayList<>();
    scripts.addAll(scriptFinder.findAll(onStartup(environment, slingSettings), resolver));
    scripts.addAll(modifiedScriptFinder.findAll(onStartupIfModified(environment, slingSettings), resolver));
    processScripts(scripts, resolver);
  }

  @Override
  public ScriptManager getScriptManager() {
    return scriptManager;
  }

}
