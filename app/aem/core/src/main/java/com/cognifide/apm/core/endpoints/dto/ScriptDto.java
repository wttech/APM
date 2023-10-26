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
package com.cognifide.apm.core.endpoints.dto;

import com.cognifide.apm.api.scripts.Script;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;

public class ScriptDto {

  private final String name;

  private final String path;

  private final String author;

  private final boolean launchEnabled;

  private final String launchMode;

  private final Date lastModified;

  private final boolean valid;

  public ScriptDto(Script script) {
    this.name = FilenameUtils.getName(script.getPath());
    this.path = script.getPath();
    this.author = script.getAuthor();
    this.launchEnabled = script.isLaunchEnabled();
    this.launchMode = script.getLaunchMode().name().toLowerCase();
    this.lastModified = script.getLastModified();
    this.valid = script.isValid();
  }

  public String getPath() {
    return path;
  }
}
