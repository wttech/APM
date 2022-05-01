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
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.Arrays;
import java.util.List;
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
        Property.DESCRIPTION + "Launches configured scripts",
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

  @Activate
  public void activate(Configuration config) {
    SlingHelper.operateTraced(resolverProvider, resolver -> processScripts(config.scriptPaths(), resolver));
  }

  private void processScripts(String[] scriptPaths, ResourceResolver resolver) throws PersistenceException {
    List<Script> scripts = Arrays.stream(scriptPaths)
        .map(scriptPath -> scriptFinder.find(scriptPath, resolver))
        .collect(Collectors.toList());
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
  }

}
