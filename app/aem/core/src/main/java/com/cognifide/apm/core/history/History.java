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
package com.cognifide.apm.core.history;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.core.logger.Progress;
import java.util.Calendar;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

public interface History {

  /**
   * Save detailed script execution on a remote host as entry
   */
  HistoryEntry logRemote(Script script, ExecutionMode mode, Progress progressLogger, InstanceDetails instanceDetails,
      Calendar executionTime);

  /**
   * Save detailed script execution as entry
   */
  HistoryEntry logLocal(Script script, ExecutionMode mode, Progress progressLogger);

  /**
   * Replicate log entry from publish to author instance
   */
  void replicate(HistoryEntry entry, String executor) throws RepositoryException;

  List<Resource> findAllResources(ResourceResolver resourceResolver);

  List<HistoryEntry> findAllHistoryEntries(ResourceResolver resourceResolver);

  HistoryEntry findHistoryEntry(ResourceResolver resourceResolver, String path);

  @NotNull
  ScriptHistory findScriptHistory(ResourceResolver resourceResolver, Script script);
}
