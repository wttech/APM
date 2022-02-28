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

import static com.cognifide.apm.core.scripts.ScriptFilters.onSchedule;

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.launchers.ScheduledScriptLauncher.ScheduleExecutorConfiguration;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.Date;
import java.util.List;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
    immediate = true,
    service = Runnable.class,
    property = {
        Property.DESCRIPTION + "Launches scheduled scripts",
        Property.VENDOR,
        Property.SCHEDULER + "0 * * * * ?"
    }
)
@Designate(ocd = ScheduleExecutorConfiguration.class)
public class ScheduledScriptLauncher extends AbstractLauncher implements Runnable {

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private SlingSettingsService slingSettings;

  @Reference
  private ResourceResolverFactory resolverFactory;

  private boolean enabled = true;

  @Activate
  @Modified
  public void activate(ScheduleExecutorConfiguration config) {
    enabled = !config.disableScheduleExecutor();
  }

  @Override
  public void run() {
    if (enabled) {
      SlingHelper.operateTraced(resolverFactory, this::runScheduled);
    }
  }

  private void runScheduled(ResourceResolver resolver) throws PersistenceException {
    LaunchEnvironment environment = LaunchEnvironment.of(slingSettings);
    List<Script> scripts = scriptFinder.findAll(onSchedule(environment, slingSettings, new Date()), resolver);
    processScripts(scripts, resolver, LauncherType.SCHEDULED);
  }

  @Override
  public ScriptManager getScriptManager() {
    return scriptManager;
  }

  @ObjectClassDefinition(name = "AEM Permission Management - Schedule Launcher Configuration ")
  public @interface ScheduleExecutorConfiguration {

    @AttributeDefinition(name = "Disable Schedule Launcher", defaultValue = "false")
    boolean disableScheduleExecutor();
  }
}