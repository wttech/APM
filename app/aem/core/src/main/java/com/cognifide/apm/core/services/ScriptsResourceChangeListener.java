package com.cognifide.apm.core.services;

import com.cognifide.apm.api.scripts.LaunchMode;
import com.cognifide.apm.api.scripts.Script;
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

  private Map<String, RegisterScript> registeredScripts;

  @Activate
  public void activate() {
    registeredScripts = new HashMap<>();
    Bundle currentBundle = FrameworkUtil.getBundle(ScriptsResourceChangeListener.class);
    BundleContext bundleContext = currentBundle.getBundleContext();

    SlingHelper.operateTraced(resolverProvider, resolver ->
        scriptFinder.findAll(script -> {
              LaunchMode launchMode = script.getLaunchMode();
              return launchMode == LaunchMode.ON_SCHEDULE && script.getLaunchSchedule() != null
                  || launchMode == LaunchMode.ON_CRON_EXPRESSION && StringUtils.isNotEmpty(script.getCronExpression());
            }, resolver)
            .forEach(script -> registerService(script, bundleContext))
    );
  }

  @Override
  public void onChange(List<ResourceChange> list) {
    Bundle currentBundle = FrameworkUtil.getBundle(ScriptsResourceChangeListener.class);
    BundleContext bundleContext = currentBundle.getBundleContext();

    SlingHelper.operateTraced(resolverProvider, resolver ->
        list.stream()
            .filter(resourceChange -> StringUtils.endsWith(resourceChange.getPath(), Apm.FILE_EXT))
            .forEach(resourceChange -> {
              if (resourceChange.getType() == ResourceChange.ChangeType.ADDED) {
                Script script = scriptFinder.find(resourceChange.getPath(), resolver);
                if (script != null) {
                  registerService(script, bundleContext);
                }
              } else if (resourceChange.getType() == ResourceChange.ChangeType.REMOVED) {
                RegisterScript registeredScript = registeredScripts.get(resourceChange.getPath());
                if (registeredScript != null) {
                  registeredScript.registration.unregister();
                  registeredScripts.remove(resourceChange.getPath());
                }
              } else if (resourceChange.getType() == ResourceChange.ChangeType.CHANGED) {
                Script script = scriptFinder.find(resourceChange.getPath(), resolver);
                RegisterScript registeredScript = registeredScripts.get(resourceChange.getPath());
                if (script != null && !Objects.equals(script, registeredScript.script)) {
                  registeredScript.registration.unregister();
                  registeredScripts.remove(resourceChange.getPath());
                  registerService(script, bundleContext);
                }
              }
            })
    );
  }

  private void registerService(Script script, BundleContext bundleContext) {
    Dictionary<String, Object> dictionary = new Hashtable<>();
    if (script.getLaunchMode() == LaunchMode.ON_SCHEDULE) {
      SimpleDateFormat cronExpressionFormat = new SimpleDateFormat("s m H d M ? y");
      dictionary.put("scheduler.expression", cronExpressionFormat.format(script.getLaunchSchedule()));
    } else if (script.getLaunchMode() == LaunchMode.ON_CRON_EXPRESSION) {
      dictionary.put("scheduler.expression", script.getCronExpression());
    }
    ApmInstallService service = new ApmInstallService(script.getPath(), resolverProvider, scriptManager, scriptFinder);
    ServiceRegistration<Runnable> registration = bundleContext.registerService(Runnable.class, service, dictionary);
    registeredScripts.put(script.getPath(), new RegisterScript(script, registration));
  }

  private class RegisterScript {

    private Script script;

    private ServiceRegistration<Runnable> registration;

    public RegisterScript(Script script, ServiceRegistration<Runnable> registration) {
      this.script = script;
      this.registration = registration;
    }
  }
}
