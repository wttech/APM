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

public enum LaunchMode {

  /**
   * Executed only by using user interface
   */
  ON_DEMAND,

  /**
   * Executed only after scheduled date
   */
  ON_SCHEDULE,

  /**
   * Executed always on bundle start
   */
  ON_STARTUP,

  /**
   * Executed on bundle start, only if script content's changed
   */
  ON_STARTUP_IF_MODIFIED,

  /**
   * Executed always after successfully package installation
   */
  ON_INSTALL,

  /**
   * Executed after successfully package installation, only if script content's changed
   */
  ON_INSTALL_IF_MODIFIED;

  public static Optional<LaunchMode> from(String text) {
    return Arrays.stream(LaunchMode.values())
        .filter(launchMode -> StringUtils.endsWithIgnoreCase(launchMode.name(), text))
        .findFirst();
  }
}
