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

import com.cognifide.cq.cqsm.api.history.ModifiableEntryBuilder;
import java.util.Calendar;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

@Builder
public class HistoryLogWriter {

  private String fileName;
  private String filePath;
  private String author;
  private String mode;
  private String progressLog;
  private String executor;
  private String instanceType;
  private String instanceHostname;
  private String uploadTime;
  private Calendar executionTime;

  public void writeTo(Resource historyLogResource) {
    ModifiableValueMap valueMap = historyLogResource.adaptTo(ModifiableValueMap.class);
    valueMap.put(ModifiableEntryBuilder.FILE_NAME, fileName);
    valueMap.put(ModifiableEntryBuilder.FILE_PATH_PROPERTY, filePath);
    valueMap.put(ModifiableEntryBuilder.AUTHOR, author);
    valueMap.put(ModifiableEntryBuilder.MODE, mode);
    valueMap.put(ModifiableEntryBuilder.PROGRESS_LOG_PROPERTY, progressLog);
    valueMap.put(ModifiableEntryBuilder.INSTANCE_TYPE_PROPERTY, instanceType);
    valueMap.put(ModifiableEntryBuilder.INSTANCE_HOSTNAME_PROPERTY, instanceHostname);
    valueMap.put(ModifiableEntryBuilder.UPLOAD_TIME, uploadTime);
    valueMap.put(ModifiableEntryBuilder.EXECUTION_TIME_PROPERTY, executionTime);
    if (StringUtils.isNotBlank(executor)) {
      valueMap.put(ModifiableEntryBuilder.EXECUTOR_PROPERTY, executor);
    }
  }
}
