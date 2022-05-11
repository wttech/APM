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
package com.cognifide.apm.core.ui.models;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.history.HistoryEntry;
import com.cognifide.apm.core.history.ScriptHistory;
import com.cognifide.apm.core.scripts.ScriptModel;
import com.cognifide.apm.core.utils.CalendarUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

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
  private final List<ScriptRun> runs = new ArrayList<>();

  @Getter
  private String launchMode;

  @Getter
  private String launchEnvironment;

  @Getter
  private boolean isLaunchEnabled;

  @PostConstruct
  protected void afterCreated() {
    this.isFolder = isFolder(resource);
    this.scriptName = defaultIfEmpty(getProperty(resource, JcrConstants.JCR_TITLE), resource.getName());
    if (!isFolder) {
      Optional.ofNullable(resource.adaptTo(ScriptModel.class)).ifPresent(script -> {
        ScriptHistory scriptHistory = history.findScriptHistory(resource.getResourceResolver(), script);
        this.author = script.getAuthor();
        this.isValid = script.isValid();
        this.lastModified = CalendarUtils.asCalendar(script.getLastModified());
        this.runs.add(createScriptRun("dryRun", script, scriptHistory.getLastLocalDryRun()));
        this.runs.add(createScriptRun("runOnAuthor", script, scriptHistory.getLastLocalRun()));
        this.launchMode = label(script.getLaunchMode());
        this.launchEnvironment = Stream.concat(
                Stream.of(script.getLaunchEnvironment().getRunMode()),
                CollectionUtils.emptyIfNull(script.getLaunchRunModes()).stream()
            )
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.joining(", "));
        this.isLaunchEnabled = script.isLaunchEnabled();
      });
    }
  }

  public String label(Object object) {
    String words = object.toString().replace('_', ' ');
    words = WordUtils.capitalizeFully(words.toLowerCase());
    return words;
  }

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

  private static String getProperty(Resource resource, String name) {
    return resource.getValueMap().get(name, StringUtils.EMPTY);
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
