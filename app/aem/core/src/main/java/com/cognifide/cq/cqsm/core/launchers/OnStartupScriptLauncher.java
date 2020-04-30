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
package com.cognifide.cq.cqsm.core.launchers;

import static com.cognifide.cq.cqsm.core.scripts.ScriptFilters.onStart;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.cq.cqsm.api.scripts.EventListener;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import java.util.List;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    property = {
        Property.DESCRIPTION + "Launches scripts on bundle startup",
        Property.VENDOR
    }
)
public class OnStartupScriptLauncher extends AbstractScriptLauncher {

  /**
   * Reference needed for proper event hook up on activation
   */
  @Reference
  private EventListener eventListener;

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Activate
  private synchronized void activate() {
    SlingHelper.operateTraced(resolverFactory, this::runOnStartup);
  }

  private void runOnStartup(ResourceResolver resolver) throws PersistenceException {
    final List<Script> scripts = scriptFinder.findAll(onStart(), resolver);
    if (!scripts.isEmpty()) {
      logger.info("Startup script executor is trying to execute scripts on startup: {}", scripts.size());
      logger.info(MessagingUtils.describeScripts(scripts));
      for (Script script : scripts) {
        processScript(script, resolver, LauncherType.ON_STARTUP);
      }
    } else {
      logger.info("Startup script executor has nothing to do");
    }
  }

  @Override
  public ScriptManager getScriptManager() {
    return scriptManager;
  }
}
