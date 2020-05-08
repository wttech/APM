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
package com.cognifide.cq.cqsm.core.scripts;

import static org.apache.commons.lang.StringUtils.isBlank;

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.LaunchMode;
import com.cognifide.apm.api.scripts.Script;
import java.util.Date;
import java.util.function.Predicate;
import org.apache.commons.lang.StringUtils;

/**
 * Due to the ResourceResolver dependency these filters should not be used lazy
 * i.e. in a context where the resolver passed as an argument is no longer alive.
 */
public class ScriptFilters {

  public static Predicate<Script> onInstall(final LaunchEnvironment environment, final String currentHook) {
    return enabled()
        .and(withLaunchMode(LaunchMode.ON_INSTALL))
        .and(withLaunchEnvironment(environment))
        .and(withLaunchHook(currentHook));
  }

  public static Predicate<Script> onInstallModified(final LaunchEnvironment environment, final String currentHook) {
    return enabled()
        .and(withLaunchMode(LaunchMode.ON_INSTALL_MODIFIED))
        .and(withLaunchEnvironment(environment))
        .and(withLaunchHook(currentHook));
  }

  private static Predicate<Script> withLaunchHook(String currentHook) {
    return script -> isBlank(script.getLaunchHook()) || StringUtils.equals(currentHook, script.getLaunchHook());
  }

  private static Predicate<Script> withLaunchEnvironment(LaunchEnvironment environment) {
    return script -> script.getLaunchEnvironment() == LaunchEnvironment.ALL || environment == script.getLaunchEnvironment();
  }

  private static Predicate<Script> withLaunchMode(final LaunchMode mode) {
    return script -> script.getLaunchMode() == mode;
  }

  private static Predicate<Script> enabled() {
    return script -> script.isLaunchEnabled();
  }

  public static Predicate<Script> onSchedule(final Date date) {
    return enabled()
        .and(withLaunchMode(LaunchMode.ON_SCHEDULE))
        .and(script -> script.getLastExecuted() == null && script.getLaunchSchedule().before(date));
  }

  public static Predicate<Script> onModify() {
    return enabled()
        .and(withLaunchMode(LaunchMode.ON_MODIFY));
  }

  public static Predicate<Script> onStart() {
    return enabled()
        .and(withLaunchMode(LaunchMode.ON_START));
  }

  public static Predicate<Script> noChecksum() {
    return enabled()
        .and(script -> StringUtils.isBlank(script.getChecksum()));
  }
}
