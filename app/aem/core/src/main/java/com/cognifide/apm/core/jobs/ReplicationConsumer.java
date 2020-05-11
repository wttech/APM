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
package com.cognifide.apm.core.jobs;

import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.launchers.ReplicatedScriptLauncher;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(
    immediate = true,
    service = EventHandler.class,
    property = {
        EventConstants.EVENT_TOPIC + "=" + ReplicationEvent.EVENT_TOPIC,
        Property.VENDOR
    }
)
public class ReplicationConsumer implements EventHandler {

  @Reference
  private JobManager jobManager;

  @Override
  public void handleEvent(Event event) {
    ReplicationAction action = ReplicationEvent.fromEvent(event).getReplicationAction();
    Arrays.stream(action.getPaths())
        .filter(path -> path.startsWith("/conf/apm/scripts"))
        .forEach(this::run);
  }

  public void run(String scriptPath) {
    Map<String, Object> properties = Collections.singletonMap(SlingConstants.PROPERTY_PATH, scriptPath);
    jobManager.addJob(ReplicatedScriptLauncher.JOB_NAME, properties);
  }

}
