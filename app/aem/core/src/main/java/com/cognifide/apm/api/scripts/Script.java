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

import java.util.Date;
import java.util.Set;

public interface Script {

  /**
   * Get validation status
   */
  boolean isValid();

  /**
   * Check whether is ready for automatic execution
   */
  boolean isLaunchEnabled();

  /**
   * Get launch mode
   */
  LaunchMode getLaunchMode();

  LaunchEnvironment getLaunchEnvironment();

  Set<String> getLaunchRunModes();

  String getLaunchHook();

  /**
   * Get date after which script will be executed by schedule executor
   */
  Date getLaunchSchedule();

  /**
   * Get CRON expression
   */
  String getLaunchCronExpression();

  /**
   * Get last execution date
   */
  Date getLastExecuted();

  /**
   * Returns the path for the script file
   */
  String getPath();

  /**
   * Returns checksum of the current script content.
   */
  String getChecksum();

  /**
   * Return author of the file
   */
  String getAuthor();

  /**
   * Return last modified date
   */
  Date getLastModified();

  /**
   * Return copy of the file
   */
  String getData();

}
