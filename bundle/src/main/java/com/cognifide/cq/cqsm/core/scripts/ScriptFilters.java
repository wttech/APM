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

import com.cognifide.cq.cqsm.api.scripts.ExecutionEnvironment;
import com.cognifide.cq.cqsm.api.scripts.ExecutionMode;
import com.cognifide.cq.cqsm.api.scripts.Script;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Due to the ResourceResolver dependency these filters should not be used lazy
 * i.e. in a context where the resolver passed as an argument is no longer alive.
 */
public class ScriptFilters {

  public static Predicate<Script> onHook(final ExecutionEnvironment environment, final String currentHook) {
    return enabled()
        .and(withExecutionMode(ExecutionMode.ON_HOOK))
        .and(script -> script.getExecutionEnvironment() == null || environment == script.getExecutionEnvironment())
        .and(
            script -> isBlank(script.getExecutionHook()) || StringUtils.equals(currentHook, script.getExecutionHook()));
  }

  private static Predicate<Script> withExecutionMode(final ExecutionMode mode) {
    return script -> script.getExecutionMode() == mode;
  }

  private static Predicate<Script> withExecutionMode(final List<ExecutionMode> modes) {
    return script -> modes.contains(script.getExecutionMode());
  }

  private static Predicate<Script> enabled() {
    return script -> script.isExecutionEnabled();
  }

  public static Predicate filterOnSchedule(final Date date) {
    return enabled()
        .and(withExecutionMode(ExecutionMode.ON_SCHEDULE))
        .and(script -> script.getExecutionLast() == null && script.getExecutionSchedule().before(date));
  }

  public static Predicate filterOnModify(final ResourceResolver resolver) {
    return enabled()
        .and(withExecutionMode(ExecutionMode.ON_MODIFY))
        .and(script -> script.isContentModified(resolver));
  }

  public static Predicate filterOnStart(final ResourceResolver resolver) {
    return enabled()
        .and(withExecutionMode(ExecutionMode.ON_START))
        .or(filterOnModify(resolver));
  }
}
