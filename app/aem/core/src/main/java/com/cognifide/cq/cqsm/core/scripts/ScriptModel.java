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

import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_CHECKSUM;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_LAST_EXECUTED;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_LAUNCH_ENABLED;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_LAUNCH_ENVIRONMENT;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_LAUNCH_HOOK;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_LAUNCH_MODE;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_LAUNCH_SCHEDULE;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_PUBLISH_RUN;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_REPLICATED_BY;
import static com.cognifide.cq.cqsm.core.scripts.ScriptNode.APM_VERIFIED;

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.LaunchMode;
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
public class ScriptModel implements Script {

  private final String path;

  @Self
  private Resource resource;

  @Inject
  @Named(APM_LAUNCH_ENABLED)
  @Optional
  private Boolean launchEnabled;

  @Inject
  @Named(APM_LAUNCH_MODE)
  @Optional
  private String launchMode;

  @Inject
  @Named(APM_LAUNCH_ENVIRONMENT)
  @Optional
  private String launchEnvironment;

  @Inject
  @Named(APM_LAUNCH_HOOK)
  @Optional
  private String launchHook;

  @Inject
  @Named(APM_LAUNCH_SCHEDULE)
  @Optional
  private Date launchSchedule;

  @Inject
  @Named(APM_LAST_EXECUTED)
  @Optional
  private Date lastExecution;

  @Inject
  @Named(APM_CHECKSUM)
  @Optional
  private String checksum;

  @Inject
  @Named(APM_PUBLISH_RUN)
  @Optional
  private Boolean publishRun;

  @Inject
  @Named(APM_REPLICATED_BY)
  @Optional
  private String replicatedBy;

  @Inject
  @Named(APM_VERIFIED)
  @Optional
  private Boolean verified;

  @Inject
  @Named(JcrConstants.JCR_LASTMODIFIED)
  @Optional
  private Date lastModified;

  @Inject
  @Named(JcrConstants.JCR_CREATED_BY)
  @Optional
  private String author;

  private String data;

  public ScriptModel(Resource resource) {
    this.path = resource.getPath();
  }

  public Script reload() {
    return resource.adaptTo(ScriptModel.class);
  }

  @Override
  public boolean isValid() {
    return BooleanUtils.toBoolean(verified);
  }

  @Override
  public LaunchMode getLaunchMode() {
    return (launchMode == null) ? LaunchMode.ON_DEMAND : LaunchMode.valueOf(launchMode);
  }

  @Override
  public String getLaunchHook() {
    return launchHook;
  }

  @Override
  public LaunchEnvironment getLaunchEnvironment() {
    return (launchEnvironment == null) ? LaunchEnvironment.ALL : LaunchEnvironment.valueOf(launchEnvironment);
  }

  @Override
  public Date getLaunchSchedule() {
    return launchSchedule;
  }

  @Override
  public boolean isLaunchEnabled() {
    return BooleanUtils.isNotFalse(launchEnabled);
  }

  @Override
  public Date getLastExecuted() {
    return lastExecution;
  }

  @Override
  public boolean isPublishRun() {
    return BooleanUtils.toBoolean(publishRun);
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public String getChecksum() {
    return checksum;
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public String getData() {
    if (data == null) {
      data = resource.getChild(JcrConstants.JCR_CONTENT).getValueMap().get(JcrConstants.JCR_DATA, String.class);
    }
    return data;
  }

  @Override
  public String getReplicatedBy() {
    return replicatedBy;
  }
}
