/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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

package com.cognifide.cq.cqsm.core.history;

import java.util.Calendar;
import lombok.Builder;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

@Builder
public class HistoryEntryWriter {

  private String author;
  private Calendar executionTime;
  private String executor;
  private String fileName;
  private String filePath;
  private String instanceType;
  private String instanceHostname;
  private Boolean isRunSuccessful;
  private String mode;
  private String progressLog;

  public void writeTo(Resource historyLogResource) {
    ModifiableValueMap valueMap = historyLogResource.adaptTo(ModifiableValueMap.class);
    valueMap.put(HistoryEntryImpl.FILE_NAME, fileName);
    valueMap.put(HistoryEntryImpl.FILE_PATH, filePath);
    valueMap.put(HistoryEntryImpl.AUTHOR, author);
    valueMap.put(HistoryEntryImpl.MODE, mode);
    valueMap.put(HistoryEntryImpl.PROGRESS_LOG, progressLog);
    valueMap.put(HistoryEntryImpl.INSTANCE_TYPE, instanceType);
    valueMap.put(HistoryEntryImpl.INSTANCE_HOSTNAME, instanceHostname);
    valueMap.put(HistoryEntryImpl.IS_RUN_SUCCESSFUL, isRunSuccessful);
    valueMap.put(HistoryEntryImpl.EXECUTION_TIME, executionTime);
    valueMap.put(HistoryEntryImpl.EXECUTOR, executor);
  }
}
