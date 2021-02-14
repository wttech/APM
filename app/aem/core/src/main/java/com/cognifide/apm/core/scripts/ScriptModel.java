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
package com.cognifide.apm.core.scripts;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.BooleanUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.LaunchMode;
import com.cognifide.apm.api.scripts.MutableScript;
import com.cognifide.apm.core.utils.ResourceMixinUtil;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.Lists;

@Model(adaptables = Resource.class)
public class ScriptModel implements MutableScript {

  private static Logger LOGGER = LoggerFactory.getLogger(ScriptModel.class);

  private final String path;

  @Self
  private Resource resource;

  @Inject
  @Named(ScriptNode.APM_LAUNCH_ENABLED)
  @Optional
  private Boolean launchEnabled;

  @Inject
  @Named(ScriptNode.APM_LAUNCH_MODE)
  @Optional
  private String launchMode;

  @Inject
  @Named(ScriptNode.APM_LAUNCH_ENVIRONMENT)
  @Optional
  private String launchEnvironment;

  @Inject
  @Named(ScriptNode.APM_LAUNCH_RUN_MODES)
  @Optional
  private String launchRunModes;

  @Inject
  @Named(ScriptNode.APM_LAUNCH_HOOK)
  @Optional
  private String launchHook;

  @Inject
  @Named(ScriptNode.APM_LAUNCH_SCHEDULE)
  @Optional
  private Date launchSchedule;

  @Inject
  @Named(ScriptNode.APM_LAST_EXECUTED)
  @Optional
  private Date lastExecuted;

  @Inject
  @Named(ScriptNode.APM_CHECKSUM)
  @Optional
  private String checksum;

  @Inject
  @Named(ScriptNode.APM_PUBLISH_RUN)
  @Optional
  private Boolean publishRun;

  @Inject
  @Named(ScriptNode.APM_REPLICATED_BY)
  @Optional
  private String replicatedBy;

  @Inject
  @Named(ScriptNode.APM_VERIFIED)
  @Optional
  private Boolean verified;

  @Inject
  @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_LASTMODIFIED)
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

  @Override
  public boolean isValid() {
    return BooleanUtils.toBoolean(verified);
  }

  @Override
  public LaunchMode getLaunchMode() {
    return (launchMode == null) ? LaunchMode.ON_DEMAND : LaunchMode.from(launchMode)
        .orElseGet(() -> {
          LOGGER.warn("Cannot match {} to existing launch modes. Using default one", launchMode);
          return LaunchMode.ON_DEMAND;
        });
  }

  @Override
  public String getLaunchHook() {
    return launchHook;
  }

  @Override
  public LaunchEnvironment getLaunchEnvironment() {
    return (launchEnvironment == null) ? LaunchEnvironment.ALL : LaunchEnvironment.from(launchEnvironment)
        .orElseGet(() -> {
          LOGGER.warn("Cannot match {} to existing launch environments. Using default one", launchEnvironment);
          return LaunchEnvironment.ALL;
        });
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
    return lastExecuted;
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
      Resource child = resource.getChild(JcrConstants.JCR_CONTENT);
      if (child != null) {
        data = child.getValueMap().get(JcrConstants.JCR_DATA, String.class);
      } else {
        data = "";
      }
    }
    return data;
  }

  @Override
  public String getReplicatedBy() {
    return replicatedBy;
  }

  @Override
  public void setChecksum(String checksum) throws PersistenceException {
    this.checksum = checksum;
    setProperty(ScriptNode.APM_CHECKSUM, checksum);
  }

  @Override
  public void setValid(boolean flag) throws PersistenceException {
    this.verified = flag;
    setProperty(ScriptNode.APM_VERIFIED, flag);
  }

  @Override
  public void setLastExecuted(Date date) throws PersistenceException {
    this.lastExecuted = date;
    setProperty(ScriptNode.APM_LAST_EXECUTED, date);
  }

  @Override
  public void setPublishRun(boolean flag) throws PersistenceException {
    this.publishRun = flag;
    setProperty(ScriptNode.APM_PUBLISH_RUN, flag);
  }

  @Override
  public void setReplicatedBy(String userId) throws PersistenceException {
    this.replicatedBy = userId;
    setProperty(ScriptNode.APM_REPLICATED_BY, userId);
  }

  private void setProperty(String name, Object value) throws PersistenceException {
    ModifiableValueMap vm = resource.adaptTo(ModifiableValueMap.class);
    ResourceMixinUtil.addMixin(vm, ScriptNode.APM_SCRIPT);
    vm.put(name, convertValue(value));

    resource.getResourceResolver().commit();
  }

  private Object convertValue(Object obj) {
    if (obj instanceof Date) {
      Calendar calendar = new GregorianCalendar();
      calendar.setTime((Date) obj);

      return calendar;
    }

    return obj;
  }

  public static boolean isScript(Resource resource) {
    return java.util.Optional.ofNullable(resource)
        .map(child -> getArrayProperty(child, JcrConstants.JCR_MIXINTYPES).contains(ScriptNode.APM_SCRIPT))
        .orElse(false);
  }

  private static List<String> getArrayProperty(Resource resource, String name) {
    return Lists.newArrayList(resource.getValueMap().get(name, new String[]{}));
  }
}
