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
package com.cognifide.apm.core.services.event;

import com.cognifide.apm.core.Property;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;

/**
 * Responsible for tracking Script lifecycle. Collects listeners that can hook into a lifetime of scripts.
 */
@Component(
    property = {
        Property.DESCRIPTION + "APM Event Manager",
        Property.VENDOR
    }
)
public class EventManagerImpl implements EventManager {

  @Reference
  private EventAdmin eventAdmin;

  @Override
  public void trigger(ApmEvent event) {
    eventAdmin.postEvent(event.toOsgiEvent());
  }
}
