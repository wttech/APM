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
package com.cognifide.cq.cqsm.api.logger;

import static org.apache.commons.lang.StringUtils.defaultString;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class ProgressEntry {

  private final String authorizable;

  private final String command;

  private final List<String> messages;

  private final List<String> parameters;

  private final Status status;

  public ProgressEntry(String authorizable, String command, List<String> messages, List<String> parameters,
      Status status) {
    this.authorizable = defaultString(authorizable);
    this.command = defaultString(command);
    this.messages = messages != null ? ImmutableList.copyOf(messages) : Collections.emptyList();
    this.parameters = parameters != null ? ImmutableList.copyOf(parameters) : Collections.emptyList();
    this.status = status != null ? status : Status.SUCCESS;
  }
}
