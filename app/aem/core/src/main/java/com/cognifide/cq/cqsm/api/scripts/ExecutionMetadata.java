/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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

package com.cognifide.cq.cqsm.api.scripts;

import com.cognifide.apm.api.scripts.ExecutionEnvironment;
import com.cognifide.apm.api.scripts.ExecutionMode;
import java.time.LocalDateTime;

public class ExecutionMetadata {

  private final boolean executionEnabled;
  private final ExecutionMode executionMode;
  private final ExecutionEnvironment executionEnvironment;
  private final String executionHook;
  private final LocalDateTime executionSchedule;

  public static ExecutionMetadata disabled() {
    return new ExecutionMetadata(false, ExecutionMode.ON_DEMAND, null, null, null);
  }

  public static ExecutionMetadata onDemand() {
    return new ExecutionMetadata(true, ExecutionMode.ON_DEMAND, null, null, null);
  }

  public static ExecutionMetadata onModify() {
    return new ExecutionMetadata(true, ExecutionMode.ON_MODIFY, null, null, null);
  }

  public static ExecutionMetadata onStart() {
    return new ExecutionMetadata(true, ExecutionMode.ON_START, null, null, null);
  }

  public static ExecutionMetadata onHook(ExecutionEnvironment executionEnvironment, String executionHook) {
    return new ExecutionMetadata(true, ExecutionMode.ON_DEMAND, executionEnvironment, executionHook, null);
  }

  public static ExecutionMetadata onSchedule(LocalDateTime executionSchedule) {
    return new ExecutionMetadata(true, ExecutionMode.ON_DEMAND, null, null, executionSchedule);
  }

  public ExecutionMetadata(boolean executionEnabled, ExecutionMode executionMode,
      ExecutionEnvironment executionEnvironment, String executionHook, LocalDateTime executionSchedule) {
    this.executionEnabled = executionEnabled;
    this.executionMode = executionMode;
    this.executionEnvironment = executionEnvironment;
    this.executionHook = executionHook;
    this.executionSchedule = executionSchedule;
  }

  public boolean isExecutionEnabled() {
    return executionEnabled;
  }

  public ExecutionMode getExecutionMode() {
    return executionMode;
  }

  public ExecutionEnvironment getExecutionEnvironment() {
    return executionEnvironment;
  }

  public String getExecutionHook() {
    return executionHook;
  }

  public LocalDateTime getExecutionSchedule() {
    return executionSchedule;
  }
}
