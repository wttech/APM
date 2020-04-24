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

import com.cognifide.apm.api.scripts.ExecutionEnvironment;
import com.cognifide.apm.api.scripts.ExecutionMode;
import com.cognifide.apm.api.scripts.Script;
import com.day.cq.commons.jcr.JcrConstants;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.BooleanUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class)
public class ScriptImpl implements Script {

  private final String path;

  @Self
  private Resource resource;

  @Inject
  @Named(JcrConstants.JCR_CONTENT)
  private ScriptContent scriptContent;

  @Inject
  @Named(JcrConstants.JCR_CREATED_BY)
  @Optional
  private String author;

  public ScriptImpl(Resource resource) {
    this.path = resource.getPath();
  }

  public void refresh() {
    scriptContent = resource.getChild(JcrConstants.JCR_CONTENT).adaptTo(ScriptContent.class);
  }

  @Override
  public boolean isValid() {
    return BooleanUtils.toBoolean(scriptContent.getVerified());
  }

  @Override
  public ExecutionMode getExecutionMode() {
    return (scriptContent.getExecutionMode() == null) ?
        ExecutionMode.ON_DEMAND : ExecutionMode.valueOf(scriptContent.getExecutionMode());
  }

  @Override
  public String getExecutionHook() {
    return scriptContent.getExecutionHook();
  }

  @Override
  public ExecutionEnvironment getExecutionEnvironment() {
    return (scriptContent.getExecutionEnvironment() == null) ?
        null : ExecutionEnvironment.valueOf(scriptContent.getExecutionEnvironment());
  }

  @Override
  public Date getExecutionSchedule() {
    return scriptContent.getExecutionSchedule();
  }

  @Override
  public boolean isExecutionEnabled() {
    return BooleanUtils.isNotFalse(scriptContent.getExecutionEnabled());
  }

  @Override
  public Date getExecutionLast() {
    return scriptContent.getExecutionLast();
  }

  @Override
  public boolean isPublishRun() {
    return BooleanUtils.toBoolean(scriptContent.getPublishRun());
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public String getChecksum() {
    return scriptContent.getChecksum();
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public Date getLastModified() {
    return scriptContent.getLastModified();
  }

  @Override
  public String getData() {
    return scriptContent.getData();
  }

  @Override
  public String getReplicatedBy() {
    return scriptContent.getReplicatedBy();
  }
}
