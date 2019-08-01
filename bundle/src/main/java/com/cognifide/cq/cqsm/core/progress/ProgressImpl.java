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
package com.cognifide.cq.cqsm.core.progress;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

import com.cognifide.apm.antlr.argument.Arguments;
import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.logger.Message;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.logger.ProgressEntry;
import com.cognifide.cq.cqsm.api.logger.Status;
import com.cognifide.cq.cqsm.api.progress.ProgressHelper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ProgressImpl implements Progress {

  private final List<ProgressEntry> entries;

  private final String executor;

  public ProgressImpl(String executor) {
    this(executor, new LinkedList<>());
  }

  public ProgressImpl(String executor, List<ProgressEntry> entries) {
    this.executor = executor;
    this.entries = entries;
  }

  @Override
  public List<ProgressEntry> getEntries() {
    return Lists.newLinkedList(entries);
  }

  @Override
  public void addEntry(ActionDescriptor descriptor, ActionResult result) {
    this.entries.add(
        new ProgressEntry(result.getAuthorizable(),
            descriptor.getCommand(),
            toMessages(result.getMessages()),
            toParameters(descriptor.getArguments()),
            result.getStatus())
    );
  }

  private List<String> toMessages(List<Message> messages) {
    return messages.stream().map(it -> it.getText()).collect(Collectors.toList());
  }

  private List<String> toParameters(Arguments arguments) {
    final List<String> parameters = new ArrayList<>();
    arguments.getRequired().forEach(it -> parameters.add(it.toString()));
    arguments.getNamed().forEach((key, value) -> parameters.add(format("%s= %s", key, value)));
    arguments.getFlags().forEach(it -> parameters.add(format("--%s", it)));
    return parameters;
  }

  @Override
  public void addEntry(Status status, String message) {
    this.entries.add(shortEntry("", singletonList(message), status));
  }

  @Override
  public void addEntry(Status status, List<String> messages) {
    this.entries.add(shortEntry("", messages, status));
  }

  @Override
  public void addEntry(Status status, String message, String command) {
    this.entries.add(shortEntry(command, singletonList(message), status));
  }

  @Override
  public void addEntry(Status status, List<String> messages, String command) {
    this.entries.add(shortEntry(command, messages, status));
  }

  private ProgressEntry shortEntry(String command, List<String> messages, Status status) {
    return new ProgressEntry("", command, messages, Collections.emptyList(), status);
  }

  @Override
  public boolean isSuccess() {
    return ProgressHelper.hasNoErrors(entries);
  }

  @Override
  public ProgressEntry getLastError() {
    for (int i = entries.size() - 1; i >= 0; i--) {
      final ProgressEntry entry = entries.get(i);

      if (entry.getStatus().equals(Status.ERROR)) {
        return entry;
      }
    }

    return null;
  }

  @Override
  public String getExecutor() {
    return executor;
  }
}
