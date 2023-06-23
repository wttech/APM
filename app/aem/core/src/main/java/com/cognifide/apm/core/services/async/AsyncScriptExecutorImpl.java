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
package com.cognifide.apm.core.services.async;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.jobs.JobResultsCache;
import com.cognifide.apm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.apm.core.jobs.ScriptRunnerJobConsumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    property = {
        Property.DESCRIPTION + "APM Service for executing scripts in background and checking theirs status",
        Property.VENDOR
    }
)
public class AsyncScriptExecutorImpl implements AsyncScriptExecutor {

  public static final String SCRIPT_PATH = "searchPath";

  public static final String EXECUTION_MODE = "modeName";

  public static final String USER_ID = "userName";

  public static final String DEFINITIONS = "definitions";

  public static final String ID = "id";

  @Reference
  private ScriptRunnerJobConsumer scriptRunnerJobConsumer;

  @Reference
  private JobResultsCache jobResultsCache;

  @Override
  public String process(Script script, ExecutionMode executionMode, Map<String, String> customDefinitions, ResourceResolver resolver) {
    String id = UUID.randomUUID().toString();
    Map<String, Object> properties = new HashMap<>();
    properties.put(ID, id);
    properties.put(SCRIPT_PATH, script.getPath());
    properties.put(EXECUTION_MODE, executionMode.toString());
    properties.put(USER_ID, resolver.getUserID());
    properties.put(DEFINITIONS, customDefinitions);
    jobResultsCache.put(id, ExecutionSummary.running());
    new Thread(() -> scriptRunnerJobConsumer.process(properties)).start();
    return id;
  }

  @Override
  public ExecutionStatus checkStatus(String id) {
    ExecutionSummary executionSummary = jobResultsCache.get(id);
    ExecutionStatus executionStatus;
    if (executionSummary == null) {
      executionStatus = new ExecutionStatus.UnknownExecution();
    } else if (executionSummary.isFinished()) {
      executionStatus = finishedExecution(executionSummary);
    } else {
      executionStatus = new ExecutionStatus.RunningExecution();
    }
    return executionStatus;
  }

  private ExecutionStatus finishedExecution(ExecutionSummary executionSummary) {
    List<ExecutionResult.Entry> entries = executionSummary.getResult().getEntries();
    ExecutionResult.Entry errorEntry = entries.stream()
        .filter(entry -> entry.getStatus() == Status.ERROR)
        .findFirst()
        .orElse(null);
    ExecutionStatus executionStatus;
    if (errorEntry != null) {
      executionStatus = new ExecutionStatus.FinishedFailedExecution(executionSummary.getPath(), entries, errorEntry);
    } else {
      executionStatus = new ExecutionStatus.FinishedSuccessfulExecution(executionSummary.getPath(), entries);
    }
    return executionStatus;
  }
}
