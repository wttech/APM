/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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

package com.cognifide.apm.core.history;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;

public final class HistoryEntryWriter {

  private final String author;

  private final Calendar executionTime;

  private final String executor;

  private final long executionDuration;

  private final String fileName;

  private final String filePath;

  private final Boolean isRunSuccessful;

  private final String mode;

  private final String progressLog;

  private final String instanceName;

  private HistoryEntryWriter(String author, Calendar executionTime, String executor, long executionDuration, String fileName, String filePath, Boolean isRunSuccessful, String mode, String progressLog, String instanceName) {
    this.author = author;
    this.executionTime = executionTime;
    this.executor = executor;
    this.executionDuration = executionDuration;
    this.fileName = fileName;
    this.filePath = filePath;
    this.isRunSuccessful = isRunSuccessful;
    this.mode = mode;
    this.progressLog = progressLog;
    this.instanceName = instanceName;
  }

  public static HistoryEntryWriterBuilder builder() {
    return new HistoryEntryWriterBuilder();
  }

  public void writeTo(Resource historyLogResource) throws IOException {
    ModifiableValueMap valueMap = historyLogResource.adaptTo(ModifiableValueMap.class);
    valueMap.put(HistoryEntryImpl.SCRIPT_NAME, fileName);
    valueMap.put(HistoryEntryImpl.SCRIPT_PATH, filePath);
    valueMap.put(HistoryEntryImpl.AUTHOR, author);
    valueMap.put(HistoryEntryImpl.MODE, mode);
    int logWarnStringSizeThreshold = Integer.getInteger("oak.repository.node.property.logWarnStringSizeThreshold", 102400);
    if (progressLog.length() > logWarnStringSizeThreshold) {
      try (InputStream progressLogInput = IOUtils.toInputStream(progressLog, StandardCharsets.UTF_8)) {
        valueMap.put(HistoryEntryImpl.PROGRESS_LOG, progressLogInput);
      }
    } else {
      valueMap.put(HistoryEntryImpl.PROGRESS_LOG, progressLog);
    }
    valueMap.put(HistoryEntryImpl.IS_RUN_SUCCESSFUL, isRunSuccessful);
    valueMap.put(HistoryEntryImpl.EXECUTION_TIME, executionTime);
    valueMap.put(HistoryEntryImpl.EXECUTION_DURATION, executionDuration);
    valueMap.put(HistoryEntryImpl.EXECUTOR, executor);
    valueMap.put(HistoryEntryImpl.INSTANCE_NAME, instanceName);
  }

  public static class HistoryEntryWriterBuilder {

    private String author;

    private Calendar executionTime;

    private String executor;

    private long executionDuration;

    private String fileName;

    private String filePath;

    private Boolean isRunSuccessful;

    private String mode;

    private String progressLog;

    private String instanceName;

    private HistoryEntryWriterBuilder() {
      // intentionally empty
    }

    public HistoryEntryWriterBuilder author(String author) {
      this.author = author;
      return this;
    }

    public HistoryEntryWriterBuilder executionTime(Calendar executionTime) {
      this.executionTime = executionTime;
      return this;
    }

    public HistoryEntryWriterBuilder executor(String executor) {
      this.executor = executor;
      return this;
    }

    public HistoryEntryWriterBuilder executionDuration(long executionDuration) {
      this.executionDuration = executionDuration;
      return this;
    }

    public HistoryEntryWriterBuilder fileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public HistoryEntryWriterBuilder filePath(String filePath) {
      this.filePath = filePath;
      return this;
    }

    public HistoryEntryWriterBuilder isRunSuccessful(Boolean isRunSuccessful) {
      this.isRunSuccessful = isRunSuccessful;
      return this;
    }

    public HistoryEntryWriterBuilder mode(String mode) {
      this.mode = mode;
      return this;
    }

    public HistoryEntryWriterBuilder progressLog(String progressLog) {
      this.progressLog = progressLog;
      return this;
    }

    public HistoryEntryWriterBuilder instanceName(String instanceName) {
      this.instanceName = instanceName;
      return this;
    }

    public HistoryEntryWriter build() {
      return new HistoryEntryWriter(author, executionTime, executor, executionDuration, fileName, filePath, isRunSuccessful, mode, progressLog, instanceName);
    }
  }
}
