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
package com.cognifide.apm.core.launchers;

import static com.cognifide.apm.api.scripts.LaunchEnvironment.AUTHOR;
import static com.cognifide.apm.api.scripts.LaunchEnvironment.PUBLISH;
import static com.cognifide.apm.core.scripts.ScriptFilters.onSchedule;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.launchers.ScheduledScriptLauncher.ScheduleExecutorConfiguration;
import com.cognifide.apm.core.utils.InstanceTypeProvider;
import com.cognifide.apm.core.utils.sling.SlingHelper;

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
  private InstanceTypeProvider instanceTypeProvider;

  @Reference
  private ResourceResolverFactory resolverFactory;

  private boolean enabled = true;

  @Activate
  @Modified
  public void activate(ScheduleExecutorConfiguration config) {
    enabled = !config.disableScheduleExecutor();
  }

  @Override
  public synchronized void run() {
    if (enabled) {
      SlingHelper.operateTraced(resolverFactory, this::runScheduled);
    }
  }

  private void runScheduled(ResourceResolver resolver) throws PersistenceException {
    LaunchEnvironment environment = instanceTypeProvider.isOnAuthor() ? AUTHOR : PUBLISH;
    Set<String> runModes = instanceTypeProvider.getRunModes();
    List<Script> scripts = scriptFinder.findAll(onSchedule(environment, runModes, new Date()), resolver);
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