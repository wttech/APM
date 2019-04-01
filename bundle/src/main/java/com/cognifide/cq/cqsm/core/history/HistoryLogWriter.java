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

import com.cognifide.cq.cqsm.api.history.Entry;
import java.util.Calendar;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

@Builder
public class HistoryLogWriter {

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
    valueMap.put(Entry.FILE_NAME, fileName);
    valueMap.put(Entry.FILE_PATH, filePath);
    valueMap.put(Entry.AUTHOR, author);
    valueMap.put(Entry.MODE, mode);
    valueMap.put(Entry.PROGRESS_LOG, progressLog);
    valueMap.put(Entry.INSTANCE_TYPE, instanceType);
    valueMap.put(Entry.INSTANCE_HOSTNAME, instanceHostname);
    valueMap.put(Entry.IS_RUN_SUCCESSFUL, isRunSuccessful);
    valueMap.put(Entry.EXECUTION_TIME, executionTime);
    if (StringUtils.isNotBlank(executor)) {
      valueMap.put(Entry.EXECUTOR, executor);
    }
  }
}
