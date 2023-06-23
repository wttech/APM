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
package com.cognifide.apm.core.services.event;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.google.common.collect.ImmutableMap;
import org.osgi.service.event.Event;

import java.util.Map;

public abstract class ApmEvent {

  private final static String SCRIPT_LAUNCHED = "com/cognifide/apm/Script/LAUNCHED";

  private final static String SCRIPT_EXECUTED = "com/cognifide/apm/Script/EXECUTED";

  private final static String INSTALL_HOOK_EXECUTED = "com/cognifide/apm/InstallHook/EXECUTED";

  private final String topic;

  private final Map<String, Object> properties;

  private ApmEvent(String topic, Map<String, Object> properties) {
    this.topic = topic;
    this.properties = properties;
  }

  public Event toOsgiEvent() {
    return new Event(topic, properties);
  }

  public static class ScriptLaunchedEvent extends ApmEvent {

    public ScriptLaunchedEvent(Script script, ExecutionMode mode) {
      super(SCRIPT_LAUNCHED, ImmutableMap.of(
          "script", script.getPath(),
          "mode", mode.toString()
      ));
    }
  }

  public static class ScriptExecutedEvent extends ApmEvent {

    public ScriptExecutedEvent(Script script, ExecutionMode mode, boolean success) {
      super(SCRIPT_EXECUTED, ImmutableMap.of(
          "script", script.getPath(),
          "mode", mode.toString(),
          "success", success
      ));
    }
  }

  public static class InstallHookExecuted extends ApmEvent {

    public InstallHookExecuted(String hookName) {
      super(INSTALL_HOOK_EXECUTED, ImmutableMap.of(
          "installHookName", hookName
      ));
    }
  }
}
