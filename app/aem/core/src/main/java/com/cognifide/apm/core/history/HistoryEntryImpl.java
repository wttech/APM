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

import com.cognifide.apm.core.logger.ProgressEntry;
import com.cognifide.apm.core.progress.ProgressHelper;
import com.cognifide.apm.core.utils.CalendarUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HistoryEntryImpl implements HistoryEntry {

  public static final String AUTHOR = "author";

  public static final String EXECUTION_TIME = "executionTime";

  public static final String EXECUTION_DURATION = "executionDuration";

  public static final String EXECUTOR = "executor";

  public static final String SCRIPT_PATH = "scriptPath";

  public static final String SCRIPT_NAME = "scriptName";

  public static final String IS_RUN_SUCCESSFUL = "isRunSuccessful";

  public static final String MODE = "mode";

  public static final String CHECKSUM = "checksum";

  public static final String PROGRESS_LOG = "summaryJSON";

  public static final String SCRIPT_CONTENT_PATH = "scriptContentPath";

  public static final String INSTANCE_NAME = "instanceName";

  @Self
  private Resource resource;

  @Inject
  @Named(AUTHOR)
  private String author;

  @Inject
  @Named(EXECUTION_TIME)
  private Date executionTime;

  @Inject
  @Named(EXECUTION_DURATION)
  private Long executionDuration;

  @Inject
  @Named(EXECUTOR)
  private String executor;

  @Inject
  @Named(SCRIPT_PATH)
  private String scriptPath;

  @Inject
  @Named(SCRIPT_NAME)
  private String scriptName;

  @Inject
  @Named(IS_RUN_SUCCESSFUL)
  @Default(booleanValues = false)
  private boolean isRunSuccessful;

  @Inject
  @Named(MODE)
  private String mode;

  @Inject
  @Named(CHECKSUM)
  private String checksum;

  private String executionSummaryJson;

  @Inject
  @Named(SCRIPT_CONTENT_PATH)
  private String scriptContentPath;

  @Inject
  @Named(INSTANCE_NAME)
  private String instanceName;

  private String path;

  private Calendar executionTimeCalendar;

  private List<ProgressEntry> executionSummary;

  public List<ProgressEntry> getExecutionSummary() {
    if (executionSummary == null) {
      executionSummary = ProgressHelper.fromJson(getExecutionSummaryJson());
    }
    return executionSummary;
  }

  @PostConstruct
  private void afterCreated() {
    path = resource.getPath();
    executionTimeCalendar = CalendarUtils.asCalendar(executionTime);
  }

  public String getAuthor() {
    return author;
  }

  public Date getExecutionTime() {
    return executionTime;
  }

  public Long getExecutionDuration() {
    return executionDuration;
  }

  public String getExecutor() {
    return executor;
  }

  public String getScriptPath() {
    return scriptPath;
  }

  public String getScriptName() {
    return scriptName;
  }

  public boolean isRunSuccessful() {
    return isRunSuccessful;
  }

  public String getMode() {
    return mode;
  }

  public String getChecksum() {
    return checksum;
  }

  public String getExecutionSummaryJson() {
    if (executionSummaryJson == null) {
      Object progressLog = resource.getValueMap().get(PROGRESS_LOG);
      if (progressLog instanceof InputStream) {
        try {
          executionSummaryJson = IOUtils.toString((InputStream) progressLog, StandardCharsets.UTF_8);
        } catch (IOException e) {
          executionSummaryJson = "[]";
        }
      } else {
        executionSummaryJson = (String) progressLog;
      }
    }
    return executionSummaryJson;
  }

  public String getScriptContentPath() {
    return scriptContentPath;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public String getPath() {
    return path;
  }

  public Calendar getExecutionTimeCalendar() {
    return executionTimeCalendar;
  }
}
