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
package com.cognifide.apm.api.status;

public enum Status {

  ERROR("entry-error", "error", "close"),
  WARNING("entry-warning", "warning", "alert"),
  SUCCESS("entry-success", "success", "check"),
  SKIPPED("entry-warning", "skipped", "chevronRight");

  private final String className;

  private final String iconTitle;

  private final String iconType;

  Status(String className, String iconTitle, String iconType) {
    this.className = className;
    this.iconTitle = iconTitle;
    this.iconType = iconType;
  }

  public String getClassName() {
    return className;
  }

  public String getIconTitle() {
    return iconTitle;
  }

  public String getIconType() {
    return iconType;
  }
}
