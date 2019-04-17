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
import com.cognifide.cq.cqsm.core.utils.CalendarUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class)
public final class ScriptsRowModel {

  private static final Set<String> FOLDER_TYPES = ImmutableSet
      .of(JcrConstants.NT_FOLDER, "sling:OrderedFolder");

  public static final String SCRIPTS_ROW_RESOURCE_TYPE = "apm/components/scriptsRow";

  @Getter
  private String scriptName;

  @Getter
  private boolean isFolder;

  @Getter
  private boolean isValid;

  @Getter
  private String author;

  @Getter
  private Calendar lastModified;

  @Getter
  private List<ScriptRun> runs = new ArrayList<>();

  @Getter
  private boolean isExecutionEnabled;

  public ScriptsRowModel(Resource resource) {
    this.isFolder = isFolder(resource);
    this.scriptName = resource.getName();
    if (!isFolder) {
      Optional.ofNullable(resource.adaptTo(ScriptImpl.class)).ifPresent(script -> {
        this.author = script.getAuthor();
        this.isValid = script.isValid();
        this.lastModified = CalendarUtils.asCalendar(script.getLastModified());
        this.runs.add(new ScriptRun("runOnAuthor", script.getRunSummary(), script.isRunSuccessful(), script.getRunTime()));
        this.runs.add(new ScriptRun("runOnPublish", script.getRunOnPublishSummary(), script.isRunOnPublishSuccessful(), script.getRunOnPublishTime()));
        this.runs.add(new ScriptRun("dryRun", script.getDryRunSummary(), script.isDryRunSuccessful(), script.getDryRunTime()));
        this.isExecutionEnabled = script.isExecutionEnabled();
      });
    }
  }

  public static boolean isFolder(Resource resource) {
    return FOLDER_TYPES.contains(resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, StringUtils.EMPTY));
  }

  public String getResourceType() {
    return SCRIPTS_ROW_RESOURCE_TYPE;
  }

  @Getter
  public static class ScriptRun {

    private final String type;
    private final String summary;
    private final boolean success;
    private final Calendar time;

    public ScriptRun(String type, String summary, boolean success, Date time) {
      this.type = type;
      this.summary = summary;
      this.success = success;
      this.time = CalendarUtils.asCalendar(time);
    }

  }
}
