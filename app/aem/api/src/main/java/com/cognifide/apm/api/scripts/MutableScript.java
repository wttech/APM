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
import org.apache.sling.api.resource.PersistenceException;

public interface MutableScript extends Script {

  /**
   * Set validation status
   */
  void setValid(boolean flag) throws PersistenceException;

  /**
   * Mark after execution
   */
  void setLastExecuted(Date date) throws PersistenceException;

  /**
   * Set publish run
   */
  void setPublishRun(boolean flag) throws PersistenceException;

  /**
   * Set replicated by user
   */
  void setReplicatedBy(String userId) throws PersistenceException;

  /**
   * Set checksum
   */
  void setChecksum(String checksum) throws PersistenceException;
}
