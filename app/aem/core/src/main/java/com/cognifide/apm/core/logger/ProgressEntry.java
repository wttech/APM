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
package com.cognifide.apm.core.logger;

import com.cognifide.apm.api.services.ExecutionResult.Entry;
import com.cognifide.apm.api.status.Status;
import java.util.List;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class ProgressEntry implements Entry {

  private final String authorizable;

  private final Position position;

  private final String command;

  private final List<String> messages;

  private final List<String> parameters;

  private final Status status;

  public ProgressEntry(Status status, List<String> messages, String command, String authorizable,
      List<String> parameters, Position position) {
    this.status = status != null ? status : Status.SUCCESS;
    this.command = StringUtils.defaultString(command);
    this.position = position;
    this.messages = ListUtils.emptyIfNull(messages);
    this.parameters = ListUtils.emptyIfNull(parameters);
    this.authorizable = StringUtils.defaultString(authorizable);
  }

  @Override
  public String getAuthorizable() {
    return authorizable;
  }

  public Position getPosition() {
    return position;
  }

  @Override
  public String getCommand() {
    return command;
  }

  @Override
  public List<String> getMessages() {
    return messages;
  }

  @Override
  public List<String> getParameters() {
    return parameters;
  }

  @Override
  public Status getStatus() {
    return status;
  }
}
