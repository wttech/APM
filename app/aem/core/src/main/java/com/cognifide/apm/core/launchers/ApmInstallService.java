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
import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.utils.RuntimeUtils;
import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.util.Collections;

public class ApmInstallService extends AbstractLauncher implements Runnable {

  private final String scriptPath;

  private final ResourceResolverProvider resolverProvider;

  private final ScriptManager scriptManager;

  private final ScriptFinder scriptFinder;

  public ApmInstallService(String scriptPath, ResourceResolverProvider resolverProvider, ScriptManager scriptManager, ScriptFinder scriptFinder) {
    this.scriptPath = scriptPath;
    this.resolverProvider = resolverProvider;
    this.scriptManager = scriptManager;
    this.scriptFinder = scriptFinder;
  }

  @Override
  public void run() {
    SlingHelper.operateTraced(resolverProvider, resolver -> {
      if (RuntimeUtils.isMutableContentInstance(resolver)) {
        Script script = scriptFinder.find(scriptPath, resolver);
        processScripts(Collections.singletonList(script), resolver);
      }
    });
  }

  @Override
  protected ScriptManager getScriptManager() {
    return scriptManager;
  }
}
