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

import com.cognifide.apm.api.scripts.LaunchMode;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ModifiableScript;
import com.cognifide.cq.cqsm.core.utils.ResourceMixinUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class ModifiableScriptWrapper implements ModifiableScript {

  private final ResourceResolver resolver;

  private final ScriptModel script;

  public ModifiableScriptWrapper(ResourceResolver resolver, Script script) {
    this.resolver = resolver;
    if (script instanceof ScriptModel) {
      this.script = (ScriptModel) script;
    } else {
      this.script = null;
    }
  }

  @Override
  public void setLaunchSchedule(Date date) throws PersistenceException {
    setProperty(ScriptNode.APM_LAUNCH_SCHEDULE, date);
    if (script != null) {
      script.setLaunchSchedule(date);
    }
  }

  @Override
  public void setExecuted(boolean flag) throws PersistenceException {
    final Date date = flag ? new Date() : null;
    setProperty(ScriptNode.APM_LAST_EXECUTED, date);
    if (script != null) {
      script.setLastExecution(date);
    }
  }

  @Override
  public void setLaunchEnabled(boolean flag) throws PersistenceException {
    setProperty(ScriptNode.APM_LAUNCH_ENABLED, flag);
    if (script != null) {
      script.setLaunchEnabled(flag);
    }
  }

  @Override
  public void setPublishRun(boolean flag) throws PersistenceException {
    setProperty(ScriptNode.APM_PUBLISH_RUN, flag);
    if (script != null) {
      script.setPublishRun(flag);
    }
  }

  @Override
  public void setReplicatedBy(String userId) throws PersistenceException {
    setProperty(ScriptNode.APM_REPLICATED_BY, userId);
    if (script != null) {
      script.setReplicatedBy(userId);
    }
  }

  @Override
  public void setLaunchMode(LaunchMode mode) throws PersistenceException {
    setProperty(ScriptNode.APM_LAUNCH_MODE, mode.name());
    if (script != null) {
      script.setLaunchMode(mode);
    }
  }

  @Override
  public void setValid(boolean flag) throws PersistenceException {
    setProperty(ScriptNode.APM_VERIFIED, flag);
    if (script != null) {
      script.setVerified(flag);
    }
  }

  @Override
  public void setChecksum(String checksum) throws PersistenceException {
    setProperty(ScriptNode.APM_CHECKSUM, checksum);
    if (script != null) {
      script.setChecksum(checksum);
    }
  }

  public void setProperty(String name, Object value) throws PersistenceException {
    if (script != null) {
      Resource resource = resolver.getResource(script.getPath());
      ModifiableValueMap vm = resource.adaptTo(ModifiableValueMap.class);
      ResourceMixinUtil.addMixin(vm, ScriptNode.APM_SCRIPT);
      vm.put(name, convertValue(value));

      resource.getResourceResolver().commit();
    }
  }

  private Object convertValue(Object obj) {
    if (obj instanceof Date) {
      Calendar calendar = new GregorianCalendar();
      calendar.setTime((Date) obj);

      return calendar;
    }

    return obj;
  }

}
