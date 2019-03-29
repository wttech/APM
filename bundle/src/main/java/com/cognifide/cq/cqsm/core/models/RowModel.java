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
package com.cognifide.cq.cqsm.core.models;

import com.cognifide.cq.cqsm.core.scripts.ScriptImpl;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class)
public class RowModel {

  private static final Set<String> FOLDER_TYPES = ImmutableSet
      .of(JcrConstants.NT_FOLDER, "sling:OrderedFolder");

  public static final String ROW_MODEL_RESOURCE_TYPE = "apm/components/scriptsRow";

  @Self
  private Resource resource;

  private ScriptImpl script;

  @Getter
  private String scriptName;

  @Getter
  private boolean isFolder;

  @Getter
  private boolean isValid;

  @Getter
  private String author;

  @Getter
  private Calendar executionSchedule;

  @Getter
  private Calendar lastModified;

  @Getter
  private String executionSummary;

  @Getter
  private Calendar executionLast;

  @Getter
  private String dryRunSummary;

  @Getter
  private Calendar dryRunLast;

  @Getter
  private boolean isExecutionEnabled;


  @PostConstruct
  public void init() {
    this.isFolder = FOLDER_TYPES
        .contains(resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, StringUtils.EMPTY));
    this.scriptName = resource.getName();
    if (!isFolder) {
      this.script = resource.adaptTo(ScriptImpl.class);
      Optional.ofNullable(script).ifPresent(scriptVal -> {
        this.author = scriptVal.getAuthor();
        this.isValid = scriptVal.isValid();
        this.executionLast = asCalendar(scriptVal.getExecutionLast());
        this.executionSchedule = asCalendar(scriptVal.getExecutionSchedule());
        this.lastModified = asCalendar(scriptVal.getLastModified());
        this.executionSummary = scriptVal.getExecutionSummary();
        this.dryRunSummary = scriptVal.getDryRunSummary();
        this.dryRunLast = asCalendar(scriptVal.getDryRunLast());
        this.isExecutionEnabled = scriptVal.isExecutionEnabled();
      });
    }
  }

  public String getResourceType() {
    return ROW_MODEL_RESOURCE_TYPE;
  }

  private Calendar asCalendar(Date date) {
    return Optional.ofNullable(date).map(dateVal -> {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return calendar;
    }).orElse(null);
  }
}
