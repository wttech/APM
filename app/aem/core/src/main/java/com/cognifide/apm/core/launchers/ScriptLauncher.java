package com.cognifide.apm.core.launchers;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = ScriptLauncher.class,
    property = {
        Property.DESCRIPTION + "APM Launches scripts",
        Property.VENDOR
    }
)
public class ScriptLauncher extends AbstractLauncher {

  @Reference
  private ResourceResolverProvider resolverProvider;

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ScriptFinder scriptFinder;

  public void process(String scriptPath) {
    process(Collections.singletonList(scriptPath));
  }

  public void process(List<String> scriptPaths) {
    SlingHelper.operateTraced(resolverProvider, resolver -> {
      List<Script> scripts = scriptPaths.stream()
          .map(scriptPath -> scriptFinder.find(scriptPath, resolver))
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
      processScripts(scripts, resolver);
    });
  }

  @Override
  protected ScriptManager getScriptManager() {
    return scriptManager;
  }
}
