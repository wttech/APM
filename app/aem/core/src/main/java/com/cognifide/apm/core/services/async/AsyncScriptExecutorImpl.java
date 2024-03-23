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
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.jobs.JobResultsCache;
import com.cognifide.apm.core.jobs.JobResultsCache.ExecutionSummary;
import com.cognifide.apm.core.jobs.ScriptRunnerJobConsumer;
import com.cognifide.apm.core.ui.utils.DateFormatter;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
  public String process(Script script, ExecutionMode executionMode, Map<String, String> customDefinitions, String executor) {
    String id = UUID.randomUUID().toString();
    Map<String, Object> properties = ImmutableMap.of(
        ID, id,
        SCRIPT_PATH, script.getPath(),
        EXECUTION_MODE, executionMode.toString(),
        USER_ID, executor,
        DEFINITIONS, customDefinitions
    );
    jobResultsCache.put(id, ExecutionSummary.running());
    new Thread(() -> scriptRunnerJobConsumer.process(properties)).start();
    return id;
  }

  @Override
  public ExecutionStatus checkStatus(String id) {
    ExecutionSummary executionSummary = jobResultsCache.get(id);
    if (executionSummary == null) {
      return new ExecutionStatus.Unknown();
    } else if (executionSummary.isFinished()) {
      return finishedExecution(executionSummary);
    } else {
      return new ExecutionStatus.Running();
    }
  }

  private ExecutionStatus finishedExecution(ExecutionSummary executionSummary) {
    String path = executionSummary.getPath();
    long timestamp = executionSummary.getResult().getStartTime().getTimeInMillis();
    String formattedDate = DateFormatter.format(executionSummary.getResult().getStartTime());
    List<ExecutionResult.Entry> entries = executionSummary.getResult().getEntries();
    ExecutionResult.Entry errorEntry = executionSummary.getResult().getLastError();
    if (errorEntry != null) {
      return new ExecutionStatus.Failed(path, timestamp, formattedDate, entries, errorEntry);
    } else {
      return new ExecutionStatus.Successful(path, timestamp, formattedDate, entries);
    }
  }
}
