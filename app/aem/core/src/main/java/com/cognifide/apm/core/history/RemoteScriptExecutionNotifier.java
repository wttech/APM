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
package com.cognifide.apm.core.history;

import com.cognifide.actions.api.ActionSendException;
import com.cognifide.actions.api.ActionSubmitter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(
    service = RemoteScriptExecutionNotifier.class
)
@Slf4j
public class RemoteScriptExecutionNotifier {

  public static final String REPLICATE_ACTION = "com/cognifide/actions/cqsm/history/replicate";

  @Reference(cardinality = ReferenceCardinality.OPTIONAL)
  private ActionSubmitter actionSubmitter;

  public void notifyRemoteInstance(Map<String, Object> properties) {
    if (actionSubmitter != null) {
      try {
        log.info("Sending action {} to action submitter", RemoteScriptExecutionNotifier.REPLICATE_ACTION);
        actionSubmitter.sendAction(REPLICATE_ACTION, properties);
        log.info("Action {} was sent to action submitter", RemoteScriptExecutionNotifier.REPLICATE_ACTION);
      } catch (ActionSendException e) {
        log.warn("Cannot send action", e);
      }
    }
  }
}
