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

import static com.cognifide.apm.core.scripts.ScriptFilters.onScheduleOrCronExpression;

import com.cognifide.apm.api.scripts.LaunchMode;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.RunModesProvider;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Apm;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.launchers.ApmInstallService;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    property = {
        Property.RESOURCE_PATH + "/conf/apm/scripts",
        Property.CHANGE_TYPE + "ADDED",
        Property.CHANGE_TYPE + "CHANGED",
        Property.CHANGE_TYPE + "REMOVED",
        Property.VENDOR
    }
)
public class ScriptsResourceChangeListener implements ResourceChangeListener {

  @Reference
  private ResourceResolverProvider resolverProvider;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private RunModesProvider runModesProvider;

  private Map<String, RegisterScript> registeredScripts;

  @Activate
  public void activate(BundleContext bundleContext) {
    registeredScripts = new HashMap<>();

    SlingHelper.operateTraced(resolverProvider, resolver ->
        scriptFinder.findAll(onScheduleOrCronExpression(runModesProvider), resolver)
            .forEach(script -> registerScript(script, bundleContext))
    );
  }

  @Deactivate
  public void deactivate() {
    registeredScripts.values()
        .forEach(registeredScript -> registeredScript.registration.unregister());
    registeredScripts.clear();
  }

  @Override
  public void onChange(List<ResourceChange> changes) {
    Bundle currentBundle = FrameworkUtil.getBundle(ScriptsResourceChangeListener.class);
    BundleContext bundleContext = currentBundle.getBundleContext();

    SlingHelper.operateTraced(resolverProvider, resolver ->
        changes.stream()
            .filter(change -> StringUtils.endsWith(change.getPath(), Apm.FILE_EXT))
            .forEach(change -> {
              if (change.getType() == ResourceChange.ChangeType.ADDED) {
                Script script = scriptFinder.find(change.getPath(), resolver);
                if (onScheduleOrCronExpression(runModesProvider).test(script)) {
                  registerScript(script, bundleContext);
                }
              } else if (change.getType() == ResourceChange.ChangeType.REMOVED) {
                RegisterScript registeredScript = registeredScripts.get(change.getPath());
                if (registeredScript != null) {
                  registeredScript.registration.unregister();
                  registeredScripts.remove(change.getPath());
                }
              } else if (change.getType() == ResourceChange.ChangeType.CHANGED) {
                Script script = scriptFinder.find(change.getPath(), resolver);
                RegisterScript registeredScript = registeredScripts.get(change.getPath());
                if (onScheduleOrCronExpression(runModesProvider).test(script) && !Objects.equals(script, registeredScript.script)) {
                  registeredScript.registration.unregister();
                  registeredScripts.remove(change.getPath());
                  registerScript(script, bundleContext);
                }
              }
            })
    );
  }

  private void registerScript(Script script, BundleContext bundleContext) {
    ApmInstallService service = new ApmInstallService(script.getPath(), resolverProvider, scriptManager, scriptFinder);
    Dictionary<String, Object> dictionary = new Hashtable<>();
    if (script.getLaunchMode() == LaunchMode.ON_SCHEDULE) {
      SimpleDateFormat cronExpressionFormat = new SimpleDateFormat("s m H d M ? y");
      dictionary.put("scheduler.expression", cronExpressionFormat.format(script.getLaunchSchedule()));
    } else if (script.getLaunchMode() == LaunchMode.ON_CRON_EXPRESSION) {
      dictionary.put("scheduler.expression", script.getLaunchCronExpression());
    }
    ServiceRegistration<Runnable> registration = bundleContext.registerService(Runnable.class, service, dictionary);
    registeredScripts.put(script.getPath(), new RegisterScript(script, registration));
  }

  private static class RegisterScript {

    private final Script script;

    private final ServiceRegistration<Runnable> registration;

    public RegisterScript(Script script, ServiceRegistration<Runnable> registration) {
      this.script = script;
      this.registration = registration;
    }
  }
}
