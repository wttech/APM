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
package com.cognifide.apm.core.events;

import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.jobs.ScriptUpdateJobConsumer;
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
        EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*",
        EventConstants.EVENT_FILTER + "=(path=/conf/apm/scripts/*)",
        Property.VENDOR
    }
)
public class ScriptUpdateEventHandler implements EventHandler {

  @Reference
  private JobManager jobManager;

  @Override
  public void handleEvent(Event event) {
    jobManager.addJob(ScriptUpdateJobConsumer.JOB_NAME, null);
  }

}

