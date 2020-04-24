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
package com.cognifide.cq.cqsm.core.scripts.listeners;

import com.cognifide.apm.api.services.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.Event;
import com.cognifide.cq.cqsm.api.scripts.EventListener;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptManager;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true
)
public class PredefinedDefinitionsListener implements EventListener {

  @Reference
  private ScriptManager scriptManager;

  @Activate
  private void activate() {
    scriptManager.getEventManager().addListener(Event.INIT_DEFINITIONS, this);
  }

  @Override
  public void handle(Script script, Mode mode, Progress progress) {
    final Map<String, String> definitions = new HashMap<>();

    // Modules
    definitions.put("Websites", "/libs/wcm/core/content/siteadmin");
    definitions.put("Digital Assets", "/libs/wcm/core/content/damadmin");
    definitions.put("Communities", "/libs/collab/core/content/admin");
    definitions.put("Campaigns", "/libs/mcm/content/admin");
    definitions.put("Inbox", "/libs/cq/workflow/content/inbox");
    definitions.put("Users", "/libs/cq/security/content/admin");
    definitions.put("Tools", "/libs/wcm/core/content/misc");
    definitions.put("Tagging", "/libs/cq/tagging/content/tagadmin");

    // Resources
    definitions.put("CloudServices", "/libs/cq/core/content/welcome/resources/cloudservices");
    definitions.put("Workflows", "/libs/cq/core/content/welcome/resources/workflows");
    definitions.put("TaskManagement", "/libs/cq/core/content/welcome/resources/taskmanager");
    definitions.put("Replication", "/libs/cq/core/content/welcome/resources/replication");
    definitions.put("Reports", "/libs/cq/core/content/welcome/resources/reports");
    definitions.put("Publications", "/libs/cq/core/content/welcome/resources/publishingadmin");
    definitions.put("Manuscripts", "/libs/cq/core/content/welcome/resources/manuscriptsadmin");

    // Docs
    definitions.put("Documentation", "/libs/cq/core/content/welcome/docs/docs");
    definitions.put("DeveloperResources", "/libs/cq/core/content/welcome/docs/dev");

    // Features
    definitions.put("CRXDELite", "/libs/cq/core/content/welcome/features/crxde");
    definitions.put("Packages", "/libs/cq/core/content/welcome/features/packages");
    definitions.put("PackageShare", "/libs/cq/core/content/welcome/features/share");
    definitions.put("Clustering", "/libs/cq/core/content/welcome/features/cluster");
    definitions.put("Backup", "/libs/cq/core/content/welcome/features/backup");
    definitions.put("OSGiConsole", "/libs/cq/core/content/welcome/features/config");
    definitions.put("OSGiConsoleStatusDump", "/libs/cq/core/content/welcome/features/statusdump");

    scriptManager.getPredefinedDefinitions().putAll(definitions);
  }
}
