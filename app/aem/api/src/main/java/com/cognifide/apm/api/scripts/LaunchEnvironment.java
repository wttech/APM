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
package com.cognifide.apm.api.scripts;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.settings.SlingSettingsService;

public enum LaunchEnvironment {

  ALL, AUTHOR, PUBLISH;

  public static Optional<LaunchEnvironment> from(String text) {
    return Arrays.stream(LaunchEnvironment.values())
        .filter(launchEnvironment -> StringUtils.endsWithIgnoreCase(launchEnvironment.name(), text))
        .findFirst();
  }

  public static LaunchEnvironment of(SlingSettingsService slingSettings) {
    return slingSettings.getRunModes().contains("author") ? AUTHOR : PUBLISH;
  }

}
