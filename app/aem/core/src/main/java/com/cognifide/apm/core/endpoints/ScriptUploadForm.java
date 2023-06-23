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

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.LaunchMode;
import com.cognifide.apm.core.endpoints.params.DateFormat;
import com.cognifide.apm.core.endpoints.params.FileName;
import com.cognifide.apm.core.endpoints.params.RequestParameter;
import com.cognifide.apm.core.scripts.LaunchMetadata;
import com.cognifide.apm.core.scripts.ScriptNode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

import java.io.InputStream;
import java.time.LocalDateTime;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class ScriptUploadForm {

  @Inject
  @RequestParameter(value = "file", optional = false)
  private InputStream file;

  @Inject
  @RequestParameter(value = "file", optional = false)
  @FileName
  private String fileName;

  @Inject
  @RequestParameter("overwrite")
  private boolean overwrite;

  @Inject
  @RequestParameter(ScriptNode.APM_SAVE_PATH)
  private String savePath;

  @Inject
  @RequestParameter(ScriptNode.APM_LAUNCH_ENABLED)
  private boolean launchEnabled;

  @Inject
  @RequestParameter(ScriptNode.APM_LAUNCH_MODE)
  private LaunchMode launchMode;

  @Inject
  @RequestParameter(ScriptNode.APM_LAUNCH_ENVIRONMENT)
  private LaunchEnvironment launchEnvironment;

  @Inject
  @RequestParameter(ScriptNode.APM_LAUNCH_RUN_MODES)
  private String[] launchRunModes;

  @Inject
  @RequestParameter(ScriptNode.APM_LAUNCH_HOOK)
  private String launchHook;

  @Inject
  @RequestParameter(ScriptNode.APM_LAUNCH_SCHEDULE)
  @DateFormat("yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime launchSchedule;

  public LaunchMetadata toLaunchMetadata() {
    return new LaunchMetadata(launchEnabled, launchMode, launchEnvironment, launchRunModes, launchHook, launchSchedule);
  }
}
