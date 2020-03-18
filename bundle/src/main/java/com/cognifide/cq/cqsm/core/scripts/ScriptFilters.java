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

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.cognifide.cq.cqsm.api.scripts.ExecutionMode;
import com.cognifide.cq.cqsm.api.scripts.Script;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AllPredicate;
import org.apache.commons.collections.functors.OrPredicate;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Due to the ResourceResolver dependency these filters should not be used lazy
 * i.e. in a context where the resolver passed as an argument is no longer alive.
 */
public class ScriptFilters {

  public static Predicate filterByExecutionMode(final ExecutionMode mode) {
    return filterByExecutionMode(Collections.singletonList(mode));
  }

  public static Predicate filterOnHook(final String environment, final String currentHook) {
    return new AllPredicate(new Predicate[]{
        filterExecutionEnabled(true),
        filterByExecutionMode(Collections.singletonList(ExecutionMode.ON_HOOK)),
        o -> {
          final Script script = (Script) o;
          return isBlankOrEquals(script.getExecutionEnvironment(), environment);
        },
        o -> {
          final Script script = (Script) o;
          return isBlankOrEquals(script.getExecutionHook(), currentHook);
        }
    });
  }

  private static boolean isBlankOrEquals(String property, String value) {
    return isBlank(property) || (isNotBlank(property) && equalsIgnoreCase(property, value));
  }

  public static Predicate filterExecutionEnabled(final boolean flag) {
    return script -> ((Script) script).isExecutionEnabled() == flag;
  }

  public static Predicate filterByExecutionMode(final List<ExecutionMode> modes) {
    return script -> modes.contains(((Script) script).getExecutionMode());
  }

  public static Predicate filterOnSchedule(final Date date) {
    return new AllPredicate(new Predicate[]{
        filterExecutionEnabled(true),
        filterByExecutionMode(ExecutionMode.ON_SCHEDULE),
        o -> {
          final Script script = (Script) o;
          return (script.getExecutionLast() == null) && script.getExecutionSchedule().before(date);
        }
    });
  }

  public static Predicate filterOnModify(final ResourceResolver resolver) {
    return new AllPredicate(new Predicate[]{
        filterExecutionEnabled(true),
        filterByExecutionMode(ExecutionMode.ON_MODIFY),
        script -> ((Script) script).isContentModified(resolver)
    });
  }

  public static Predicate filterOnStart(final ResourceResolver resolver) {
    return new AllPredicate(new Predicate[]{
        filterExecutionEnabled(true),
        new OrPredicate(
            filterByExecutionMode(ExecutionMode.ON_START),
            filterOnModify(resolver)
        )
    });
  }
}
