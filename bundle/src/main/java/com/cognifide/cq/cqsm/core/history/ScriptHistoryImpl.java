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
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
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
  public static final String LAST_CHECKSUM = "lastChecksum";

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

  @Inject
  @Getter
  @Named(LAST_CHECKSUM)
  private String lastChecksum;

  private HistoryEntry lastLocalRun;
  private HistoryEntry lastLocalDryRun;
  private HistoryEntry lastRemoteAutomaticRun;

  public static ScriptHistoryImpl empty(String scriptPath) {
    ScriptHistoryImpl scriptHistoryImpl = new ScriptHistoryImpl();
    scriptHistoryImpl.scriptPath = scriptPath;
    return scriptHistoryImpl;
  }

  @Override
  public HistoryEntry getLastLocalRun() {
    lastLocalRun = getHistoryEntry(lastLocalRun, lastLocalRunPath);
    return lastLocalRun;
  }

  @Override
  public HistoryEntry getLastLocalDryRun() {
    lastLocalDryRun = getHistoryEntry(lastLocalDryRun, lastLocalDryRunPath);
    return lastLocalDryRun;
  }

  @Override
  public HistoryEntry getLastRemoteAutomaticRun() {
    lastRemoteAutomaticRun = getHistoryEntry(lastRemoteAutomaticRun, lastRemoteAutomaticRunPath);
    return lastRemoteAutomaticRun;
  }

  private HistoryEntry getHistoryEntry(HistoryEntry entry, String historyEntryPath) {
    HistoryEntry historyEntry = entry;
    if (historyEntry == null && resource != null && historyEntryPath != null) {
      historyEntry = history.findHistoryEntry(resource.getResourceResolver(), historyEntryPath);
    }
    return historyEntry;
  }
}
