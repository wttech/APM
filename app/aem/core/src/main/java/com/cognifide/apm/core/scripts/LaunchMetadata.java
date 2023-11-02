/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.LaunchMode;
import java.time.LocalDateTime;

public class LaunchMetadata {

  private final boolean launchEnabled;

  private final LaunchMode launchMode;

  private final LaunchEnvironment launchEnvironment;

  private final String[] launchRunModes;

  private final String launchHook;

  private final LocalDateTime launchSchedule;

  private final String launchCronExpression;

  public LaunchMetadata(boolean launchEnabled, LaunchMode launchMode, LaunchEnvironment launchEnvironment,
      String[] launchRunModes, String launchHook, LocalDateTime launchSchedule, String launchCronExpression) {
    this.launchEnabled = launchEnabled;
    this.launchMode = launchMode;
    this.launchEnvironment = launchEnvironment;
    this.launchRunModes = launchRunModes;
    this.launchHook = launchHook;
    this.launchSchedule = launchSchedule;
    this.launchCronExpression = launchCronExpression;
  }

  public boolean isLaunchEnabled() {
    return launchEnabled;
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

  public String getLaunchHook() {
    return launchHook;
  }

  public LocalDateTime getLaunchSchedule() {
    return launchSchedule;
  }

  public String getLaunchCronExpression() {
    return launchCronExpression;
  }
}
