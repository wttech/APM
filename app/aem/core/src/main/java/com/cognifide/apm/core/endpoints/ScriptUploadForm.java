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
import com.cognifide.apm.core.endpoints.params.FileName;
import com.cognifide.apm.core.endpoints.params.RequestParameter;
import com.cognifide.apm.core.scripts.LaunchMetadata;
import com.cognifide.apm.core.scripts.ScriptNode;
import java.io.InputStream;
import java.time.OffsetDateTime;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = SlingHttpServletRequest.class)
public class ScriptUploadForm {

  @RequestParameter(optional = false)
  private InputStream file;

  @RequestParameter(value = "file", optional = false)
  @FileName
  private String fileName;

  @RequestParameter
  private boolean overwrite;

  @RequestParameter(ScriptNode.APM_SAVE_PATH)
  private String savePath;

  @RequestParameter(ScriptNode.APM_LAUNCH_ENABLED)
  private boolean launchEnabled;

  @RequestParameter(ScriptNode.APM_LAUNCH_MODE)
  private LaunchMode launchMode;

  @RequestParameter(ScriptNode.APM_LAUNCH_ENVIRONMENT)
  private LaunchEnvironment launchEnvironment;

  @RequestParameter(ScriptNode.APM_LAUNCH_RUN_MODES)
  private String[] launchRunModes;

  @RequestParameter(ScriptNode.APM_LAUNCH_HOOK)
  private String launchHook;

  @RequestParameter(ScriptNode.APM_LAUNCH_SCHEDULE)
  private OffsetDateTime launchSchedule;

  @RequestParameter(ScriptNode.APM_LAUNCH_CRON_EXPRESSION)
  private String launchCronExpression;

  public LaunchMetadata toLaunchMetadata() {
    return new LaunchMetadata(launchEnabled, launchMode, launchEnvironment, launchRunModes, launchHook, launchSchedule, launchCronExpression);
  }

  public String getFileName() {
    return fileName;
  }

  public String getSavePath() {
    return savePath;
  }

  public InputStream getFile() {
    return file;
  }

  public boolean isOverwrite() {
    return overwrite;
  }
}
