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
package com.cognifide.apm.core.actions;

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Message;
import com.cognifide.apm.api.status.Status;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class ActionResultImpl implements ActionResult {

  @Getter
  private String authorizable;

  @Getter
  private List<Message> messages;

  @Getter
  private Status status;

  public ActionResultImpl(String authorizable) {
    this();
    this.authorizable = authorizable;
  }

  public ActionResultImpl() {
    this.messages = new ArrayList<>();
    this.status = Status.SUCCESS;
  }

  @Override
  public void setAuthorizable(String authorizable) {
    this.authorizable = authorizable;
  }

  @Override
  public ActionResult merge(ActionResult... actionResults) {
    return merge(Lists.newArrayList(actionResults));
  }

  @Override
  public ActionResult merge(List<ActionResult> actionResults) {
    List<ActionResult> all = Lists.newArrayList();
    all.add(this);
    all.addAll(actionResults);
    ActionResultImpl result = new ActionResultImpl();
    result.authorizable = checkCommonAuthorizable(all);
    result.messages = mergeMessages(all);
    result.status = calculateStatus(all);
    return result;
  }

  @Override
  public void logMessage(final String message) {
    addMessage(Status.SUCCESS, message);
  }

  @Override
  public void logWarning(final String warning) {
    addMessage(Status.WARNING, warning);
    if (status != Status.ERROR) {
      status = Status.WARNING;
    }
  }

  @Override
  public void logError(final String error) {
    addMessage(Status.ERROR, error);
    status = Status.ERROR;
  }

  @Override
  public void changeStatus(Status status, String message) {
    this.status = status;
    addMessage(status, message);
  }

  private void addMessage(Status status, String message) {
    messages.add(new Message(message, status.name().toLowerCase()));
  }

  private static List<Message> mergeMessages(List<ActionResult> actionResults) {
    List<Message> result = new LinkedList<>();
    for (ActionResult actionResult : actionResults) {
      result.addAll(actionResult.getMessages());
    }
    return result;
  }

  private static Status calculateStatus(List<ActionResult> actionResults) {
    Status result = Status.SUCCESS;
    for (ActionResult actionResult : actionResults) {
      if ((result == Status.SUCCESS && !Status.SUCCESS.equals(actionResult.getStatus())) ||
          (result == Status.WARNING && Status.ERROR.equals(actionResult.getStatus()))) {
        result = actionResult.getStatus();
      }
    }
    return result;
  }

  private static String checkCommonAuthorizable(List<ActionResult> actionResults) {
    String pattern = actionResults.get(0).getAuthorizable();
    for (ActionResult actionResult : actionResults) {
      String current = actionResult.getAuthorizable();
      if (current != null && !StringUtils.equals(current, pattern)) {
        String error = String.format("Cannot create CompositeActionResult, mismatch of authorizables. Found: %s Expected: %s",
            actionResult.getAuthorizable(), pattern);
        throw new IllegalArgumentException(error);
      }
    }
    return pattern;
  }
}
