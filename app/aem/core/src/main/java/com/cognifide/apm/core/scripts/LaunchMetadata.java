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

package com.cognifide.apm.core.scripts;

import java.time.LocalDateTime;

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.LaunchMode;

public class LaunchMetadata {

  private final boolean executionEnabled;
  private final LaunchMode launchMode;
  private final LaunchEnvironment launchEnvironment;
  private final String[] launchRunModes;
  private final String executionHook;
  private final LocalDateTime executionSchedule;

  public LaunchMetadata(boolean executionEnabled, LaunchMode launchMode, LaunchEnvironment launchEnvironment,
                        String[] launchRunModes, String executionHook, LocalDateTime executionSchedule) {
    this.executionEnabled = executionEnabled;
    this.launchMode = launchMode;
    this.launchEnvironment = launchEnvironment;
    this.launchRunModes = launchRunModes;
    this.executionHook = executionHook;
    this.executionSchedule = executionSchedule;
  }

  public boolean isExecutionEnabled() {
    return executionEnabled;
  }

  public LaunchMode getLaunchMode() {
    return launchMode;
  }

  public LaunchEnvironment getLaunchEnvironment() {
    return launchEnvironment;
  }

  public String[] getLaunchRunModes() {
    return launchRunModes;
  }

  public String getExecutionHook() {
    return executionHook;
  }

  public LocalDateTime getExecutionSchedule() {
    return executionSchedule;
  }
}
