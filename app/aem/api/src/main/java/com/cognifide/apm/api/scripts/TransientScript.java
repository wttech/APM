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

public class TransientScript implements MutableScript {

  private final String path;
  private final String content;
  private String checksum;

  private TransientScript(String path, String content) {
    this.path = path;
    this.content = content;
  }

  public static Script create(String path, String content) {
    return new TransientScript(path, content);
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public String getData() {
    return content;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public boolean isLaunchEnabled() {
    return true;
  }

  @Override
  public LaunchMode getLaunchMode() {
    return LaunchMode.ON_DEMAND;
  }

  @Override
  public LaunchEnvironment getLaunchEnvironment() {
    return LaunchEnvironment.ALL;
  }

  @Override
  public Set<String> getLaunchRunModes() {
    return null;
  }

  @Override
  public String getLaunchHook() {
    return null;
  }

  @Override
  public Date getLaunchSchedule() {
    return null;
  }

  @Override
  public Date getLastExecuted() {
    return null;
  }

  @Override
  public boolean isPublishRun() {
    return false;
  }

  @Override
  public String getChecksum() {
    return checksum;
  }

  @Override
  public String getAuthor() {
    return null;
  }

  @Override
  public Date getLastModified() {
    return null;
  }

  @Override
  public void setValid(boolean flag) {

  }

  @Override
  public void setLastExecuted(Date date) {

  }

  @Override
  public void setPublishRun(boolean flag) {

  }

  @Override
  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }
}
