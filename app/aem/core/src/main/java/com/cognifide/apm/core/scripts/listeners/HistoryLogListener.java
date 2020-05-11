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
package com.cognifide.apm.core.scripts.listeners;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ExecutionMode;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.history.History;
import com.cognifide.apm.core.logger.Progress;
import com.cognifide.apm.core.scripts.Event;
import com.cognifide.apm.core.scripts.EventListener;
import com.cognifide.apm.core.scripts.ExtendedScriptManager;
import com.cognifide.apm.core.utils.InstanceTypeProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		service = EventListener.class,
		property = {
				Property.DESCRIPTION + "CQSM History Log Service",
				Property.VENDOR
		}
)
public class HistoryLogListener implements EventListener {

  @Reference
  private ExtendedScriptManager scriptManager;

  @Reference
  private InstanceTypeProvider instanceTypeProvider;

  @Reference
  private History history;

  @Activate
  private void activate() {
    if (instanceTypeProvider.isOnAuthor()) {
      scriptManager.getEventManager().addListener(Event.AFTER_EXECUTE, this);
    }
  }

  @Override
  public void handle(Script script, ExecutionMode mode, Progress progress) {
    if (mode != ExecutionMode.VALIDATION) {
      history.logLocal(script, mode, progress);
    }
  }
}
