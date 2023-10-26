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

package com.cognifide.apm.core.endpoints;

import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.core.endpoints.params.RequestParameter;
import java.util.Map;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = SlingHttpServletRequest.class)
public class ScriptExecutionForm {

  @Inject
  @RequestParameter(value = "script", optional = false)
  private String script;

  @Inject
  @RequestParameter(value = "executionMode", optional = false)
  private ExecutionMode executionMode;

  @Inject
  @RequestParameter("async")
  private boolean async;

  @Inject
  @RequestParameter("define")
  private Map<String, String> customDefinitions;

  public String getScript() {
    return script;
  }

  public ExecutionMode getExecutionMode() {
    return executionMode;
  }

  public boolean isAsync() {
    return async;
  }

  public Map<String, String> getCustomDefinitions() {
    return customDefinitions;
  }
}
