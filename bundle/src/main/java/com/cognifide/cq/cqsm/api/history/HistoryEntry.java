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
package com.cognifide.cq.cqsm.api.history;


import com.cognifide.cq.cqsm.api.logger.ProgressEntry;
import com.cognifide.cq.cqsm.api.progress.ProgressHelper;
import com.google.common.collect.ComparisonChain;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@EqualsAndHashCode
public class HistoryEntry implements Comparable<HistoryEntry> {

  public static final String AUTHOR = "author";
  public static final String EXECUTION_TIME = "executionTime";
  public static final String EXECUTOR = "executor";
  public static final String FILE_PATH = "filePath";
  public static final String FILE_NAME = "fileName";
  public static final String INSTANCE_HOSTNAME = "instanceHostname";
  public static final String INSTANCE_TYPE = "instanceType";
  public static final String IS_RUN_SUCCESSFUL = "isRunSuccessful";
  public static final String MODE = "mode";
  public static final String PROGRESS_LOG = "summaryJSON";
  public static final String UPLOAD_TIME = "uploadTime";

  @Self
  private Resource resource;

  @Inject
  @Named(AUTHOR)
  private String author;

  @Inject
  @Named(EXECUTION_TIME)
  private Date executionTime;

  @Inject
  @Named(EXECUTOR)
  private String executor;

  @Inject
  @Named(FILE_PATH)
  private String filePath;

  @Inject
  @Named(FILE_NAME)
  private String fileName;

  @Inject
  @Named(INSTANCE_HOSTNAME)
  private String instanceHostname;

  @Inject
  @Named(INSTANCE_TYPE)
  private String instanceType;

  @Inject
  @Named(IS_RUN_SUCCESSFUL)
  private Boolean isRunSuccessful;

  @Inject
  @Named(MODE)
  private String mode;

  @Inject
  @Named(UPLOAD_TIME)
  private Date uploadTime;

  @Inject
  @Named(PROGRESS_LOG)
  private String executionSummaryJson;

  private Calendar executionTimeCalendar;

  private List<ProgressEntry> executionSummary;


  public String getPath() {
    return resource.getPath();
  }

  public List<ProgressEntry> getExecutionSummary() {
    if (this.executionSummary == null) {
      this.executionSummary = ProgressHelper.fromJson(getExecutionSummaryJson());
    }
    return this.executionSummary;
  }

  @Override
  public int compareTo(HistoryEntry other) {
    return ComparisonChain.start().compare(other.executionTime, this.executionTime).result();
  }

  @PostConstruct
  protected void init() {
    executionTimeCalendar = asCalendar(executionTime);
  }

  private static Calendar asCalendar(Date date) {
    Calendar instance = Calendar.getInstance();
    instance.setTime(date);
    return instance;
  }
}