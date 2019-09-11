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

import com.cognifide.cq.cqsm.api.history.History;
import com.cognifide.cq.cqsm.api.history.HistoryEntry;
import com.cognifide.cq.cqsm.api.history.ScriptHistory;
import com.cognifide.cq.cqsm.api.scripts.Script;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ScriptHistoryImpl implements ScriptHistory {

  public static final String SCRIPT_PATH = "scriptPath";
  public static final String LAST_LOCAL_RUN = "lastLocalRun";
  public static final String LAST_LOCAL_DRY_RUN = "lastLocalDryRun";
  public static final String LAST_REMOTE_AUTOMATIC_RUN = "lastRemoteAutomaticRun";

  @Self
  private Resource resource;

  @Inject
  private History history;

  @Inject
  @Getter
  @Named(SCRIPT_PATH)
  private String scriptPath;

  @Inject
  @Getter
  @Named(LAST_LOCAL_RUN)
  private String lastLocalRunPath;

  @Inject
  @Getter
  @Named(LAST_LOCAL_DRY_RUN)
  private String lastLocalDryRunPath;

  @Inject
  @Getter
  @Named(LAST_REMOTE_AUTOMATIC_RUN)
  private String lastRemoteAutomaticRunPath;

  @Getter
  private Script script;
  @Getter
  private HistoryEntry lastLocalRun;
  @Getter
  private HistoryEntry lastLocalDryRun;
  @Getter
  private HistoryEntry lastRemoteAutomaticRun;

  public static ScriptHistoryImpl empty(String scriptPath) {
    ScriptHistoryImpl scriptHistoryImpl = new ScriptHistoryImpl();
    scriptHistoryImpl.scriptPath = scriptPath;
    return scriptHistoryImpl;
  }

  void updateScript(Script script) {
    this.script = script;
    lastLocalRun = getHistoryEntryIfMatchesChecksum(lastLocalRunPath, script.getChecksumValue());
    lastLocalDryRun = getHistoryEntryIfMatchesChecksum(lastLocalDryRunPath, script.getChecksumValue());
    lastRemoteAutomaticRun = getHistoryEntryIfMatchesChecksum(lastRemoteAutomaticRunPath, script.getChecksumValue());
  }

  private HistoryEntry getHistoryEntryIfMatchesChecksum(String lastLocalRunPath, String checksum) {
    HistoryEntry historyEntry = history.findHistoryEntry(resource.getResourceResolver(), lastLocalRunPath);
    if (historyEntry != null && StringUtils.equals(checksum, historyEntry.getChecksum())) {
      return historyEntry;
    } else {
      return null;
    }
  }
}
