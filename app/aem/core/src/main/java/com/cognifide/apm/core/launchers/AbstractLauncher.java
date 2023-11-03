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
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.services.ScriptManager;
import java.util.List;
import java.util.stream.Collectors;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLauncher {

  protected final Logger logger;

  public AbstractLauncher() {
    logger = LoggerFactory.getLogger(this.getClass());
  }

  protected void processScripts(List<Script> scripts, ResourceResolver resolver) throws PersistenceException {
    if (!scripts.isEmpty()) {
      String scriptPathsStr = scripts.stream()
          .map(Script::getPath)
          .collect(Collectors.joining(", "));
      logger.info("Launcher will try to run following scripts ({}): {}", scripts.size(), scriptPathsStr);
      for (Script script : scripts) {
        processScript(script, resolver);
      }
    }
  }

  protected void processScript(Script script, ResourceResolver resolver) throws PersistenceException {
    String scriptPath = script.getPath();
    try {
      if (!script.isValid()) {
        getScriptManager().process(script, ExecutionMode.VALIDATION, resolver);
      }
      if (script.isValid()) {
        ExecutionResult result = getScriptManager().process(script, ExecutionMode.AUTOMATIC_RUN, resolver);
        logStatus(scriptPath, result.isSuccess());
      } else {
        logger.warn("Launcher cannot execute script which is not valid: {}", scriptPath);
      }
    } catch (RepositoryException e) {
      logger.error("Script cannot be processed because of repository error: {}", scriptPath, e);
    }
  }

  private void logStatus(String scriptPath, boolean success) {
    if (success) {
      logger.info("Script successfully executed: {}", scriptPath);
    } else {
      logger.error("Script cannot be executed properly: {}", scriptPath);
    }
  }

  protected abstract ScriptManager getScriptManager();

}
