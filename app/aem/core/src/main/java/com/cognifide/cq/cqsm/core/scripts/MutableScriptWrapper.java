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

import com.cognifide.apm.api.scripts.MutableScript;
import com.cognifide.apm.api.scripts.Script;
import java.util.Date;
import org.apache.sling.api.resource.PersistenceException;

public class MutableScriptWrapper {

  private final MutableScript script;

  public MutableScriptWrapper(Script script) {
    if (script instanceof MutableScript) {
      this.script = (MutableScript) script;
    } else {
      this.script = null;
    }
  }

  public void setExecuted(boolean flag) throws PersistenceException {
    final Date date = flag ? new Date() : null;
    if (script != null) {
      script.setLastExecuted(date);
    }
  }

  public void setPublishRun(boolean flag) throws PersistenceException {
    if (script != null) {
      script.setPublishRun(flag);
    }
  }

  public void setReplicatedBy(String userId) throws PersistenceException {
    if (script != null) {
      script.setReplicatedBy(userId);
    }
  }

  public void setValid(boolean flag) throws PersistenceException {
    if (script != null) {
      script.setValid(flag);
    }
  }

  public void setChecksum(String checksum) throws PersistenceException {
    if (script != null) {
      script.setChecksum(checksum);
    }
  }
}
