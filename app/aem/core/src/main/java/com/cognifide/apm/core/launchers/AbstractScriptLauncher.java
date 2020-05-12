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

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.ScriptManager;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractScriptLauncher {

  protected final Logger logger;

  AbstractScriptLauncher() {
    logger = LoggerFactory.getLogger(this.getClass());
  }

  void processScript(Script script, ResourceResolver resolver, LauncherType launcherType) throws PersistenceException {
    final String scriptPath = script.getPath();
    try {
      getScriptManager().process(script, ExecutionMode.VALIDATION, resolver);
      if (script.isValid()) {
        final ExecutionResult result = getScriptManager().process(script, ExecutionMode.AUTOMATIC_RUN, resolver);
        logStatus(scriptPath, result.isSuccess(), launcherType);
      } else {
        logger.warn("{} executor cannot execute script which is not valid: {}", launcherType.toString(), scriptPath);
      }
    } catch (RepositoryException e) {
      logger.error("Script cannot be processed because of repository error: {}", scriptPath, e);
    }
  }

  private void logStatus(String scriptPath, boolean success, LauncherType launcherType) {
    if (success) {
      logger.info("{} script successfully executed: {}", launcherType.toString(), scriptPath);
    } else {
      logger.error("{} script cannot be executed properly: {}", launcherType.toString(), scriptPath);
    }
  }

  protected abstract ScriptManager getScriptManager();
}
