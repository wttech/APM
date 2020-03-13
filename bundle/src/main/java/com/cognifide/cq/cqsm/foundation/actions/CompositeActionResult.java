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
package com.cognifide.cq.cqsm.foundation.actions;

import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.logger.Message;
import com.cognifide.cq.cqsm.api.logger.Status;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class CompositeActionResult extends ActionResult {

  private static final String MISMATCH_MSG = "Cannot create CompositeActionResult, mismatch of authorizables. Found: {} Expected: {}";

  public CompositeActionResult(ActionResult... args) {
    this.authorizable = checkCommonAuthorizable(args);
    this.status = calculateStatus(args);
    this.messages = mergeMessages(args);
  }

  private List<Message> mergeMessages(ActionResult[] args) {
    List<Message> result = new LinkedList<>();
    for (ActionResult actionResult : args) {
      result.addAll(actionResult.getMessages());
    }
    return result;
  }

  private Status calculateStatus(ActionResult[] args) {
    Status result = Status.SUCCESS;
    for (ActionResult actionResult : args) {
      if ((result == Status.SUCCESS && !Status.SUCCESS.equals(actionResult.getStatus())) ||
          (result == Status.WARNING && Status.ERROR.equals(actionResult.getStatus()))) {
        result = actionResult.getStatus();
      }
    }
    return result;
  }

  private String checkCommonAuthorizable(ActionResult[] args) {
    String pattern = args[0].getAuthorizable();
    for (ActionResult actionResult : args) {
      String current = actionResult.getAuthorizable();
      if (current != null && !StringUtils.equals(current, pattern)) {
        String msg = String.format(MISMATCH_MSG, actionResult.getAuthorizable(), pattern);
        throw new IllegalArgumentException(msg);
      }
    }
    return pattern;
  }
}
