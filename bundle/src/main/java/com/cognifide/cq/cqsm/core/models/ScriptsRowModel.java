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

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

import com.cognifide.cq.cqsm.api.history.History;
import com.cognifide.cq.cqsm.api.history.HistoryEntry;
import com.cognifide.cq.cqsm.api.history.ScriptHistory;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.core.scripts.ScriptContent;
import com.cognifide.cq.cqsm.core.scripts.ScriptImpl;
import com.cognifide.cq.cqsm.core.utils.CalendarUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

@Model(adaptables = Resource.class)
public final class ScriptsRowModel {

  private static final Set<String> FOLDER_TYPES = ImmutableSet
      .of(JcrConstants.NT_FOLDER, "sling:OrderedFolder", "sling:Folder");

  public static final String SCRIPTS_ROW_RESOURCE_TYPE = "apm/components/scriptsRow";

  @Self
  private Resource resource;

  @Inject
  private History history;

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

  @PostConstruct
  protected void afterCreated() {
    this.isFolder = isFolder(resource);
    this.scriptName = defaultIfEmpty(getProperty(resource, JcrConstants.JCR_TITLE), resource.getName());
    if (!isFolder) {
      Optional.ofNullable(resource.adaptTo(ScriptImpl.class)).ifPresent(script -> {
        ScriptHistory scriptHistory = history.findScriptHistory(resource.getResourceResolver(), script);
        this.author = script.getAuthor();
        this.isValid = script.isValid();
        this.lastModified = CalendarUtils.asCalendar(script.getLastModified());
        this.runs.add(createScriptRun("runOnAuthor", script, scriptHistory.getLastLocalRun()));
        this.runs.add(createScriptRun("runOnPublish", script, scriptHistory.getLastRemoteAutomaticRun()));
        this.runs.add(createScriptRun("dryRun", script, scriptHistory.getLastLocalDryRun()));
        this.isExecutionEnabled = script.isExecutionEnabled();
      });
    }
  }

  @NotNull
  private ScriptRun createScriptRun(String name, Script script, HistoryEntry historyEntry) {
    if (historyEntry != null && StringUtils.equals(historyEntry.getChecksum(), script.getChecksum())) {
      return new ScriptRun(name, historyEntry);
    } else {
      return new ScriptRun(name);
    }
  }

  public static boolean isFolder(Resource resource) {
    return FOLDER_TYPES.contains(getProperty(resource, JcrConstants.JCR_PRIMARYTYPE));
  }

  public static boolean isScript(Resource resource) {
    return Optional.ofNullable(resource.getChild(JcrConstants.JCR_CONTENT))
        .map(child -> getArrayProperty(child, JcrConstants.JCR_MIXINTYPES).contains(ScriptContent.CQSM_FILE))
        .orElse(false);
  }

  private static String getProperty(Resource resource, String name) {
    return resource.getValueMap().get(name, StringUtils.EMPTY);
  }

  private static List<String> getArrayProperty(Resource resource, String name) {
    return Lists.newArrayList(resource.getValueMap().get(name, new String[]{}));
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

    public ScriptRun(String type) {
      this.type = type;
      this.summary = null;
      this.success = false;
      this.time = null;
    }

    public ScriptRun(String type, HistoryEntry historyEntry) {
      this.type = type;
      this.summary = historyEntry.getPath();
      this.success = historyEntry.isRunSuccessful();
      this.time = CalendarUtils.asCalendar(historyEntry.getExecutionTime());
    }
  }
}
