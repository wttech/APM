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
package com.cognifide.apm.core.progress;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Message;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.core.actions.ActionDescriptor;
import com.cognifide.apm.core.grammar.argument.Arguments;
import com.cognifide.apm.core.logger.Position;
import com.cognifide.apm.core.logger.Progress;
import com.cognifide.apm.core.logger.ProgressEntry;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ProgressImpl implements Progress {

  private final List<ProgressEntry> entries;

  private final String executor;

  private final long startTime;

  public ProgressImpl(String executor) {
    this(executor, new LinkedList<>());
  }

  public ProgressImpl(String executor, List<ProgressEntry> entries) {
    this.executor = executor;
    this.entries = entries;
    this.startTime = System.currentTimeMillis();
  }

  @Override
  public List<Entry> getEntries() {
    return Lists.newLinkedList(entries);
  }

  @Override
  public void addEntry(Status status, List<String> messages, String command, String authorizable, Arguments arguments,
      Position position) {
    this.entries.add(new ProgressEntry(status, messages, command, authorizable, toParameters(arguments), position));
  }

  @Override
  public void addEntry(ActionDescriptor descriptor, ActionResult result) {
    this.entries.add(
        new ProgressEntry(result.getStatus(), toMessages(result.getMessages()), descriptor.getCommand(),
            result.getAuthorizable(), toParameters(descriptor.getArguments()), null
        )
    );
  }

  private static List<String> toMessages(List<Message> messages) {
    return messages.stream()
        .map(Message::getText)
        .collect(Collectors.toList());
  }

  private List<String> toParameters(Arguments arguments) {
    if (arguments == null) {
      return Collections.emptyList();
    }
    final List<String> parameters = new ArrayList<>();
    arguments.getRequired().forEach(it -> parameters.add(it.toString()));
    arguments.getNamed().forEach((key, value) -> parameters.add(format("%s=%s", key, value)));
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

  private static ProgressEntry shortEntry(String command, List<String> messages, Status status) {
    return new ProgressEntry(status, messages, command, "", Collections.emptyList(), null);
  }

  @Override
  public boolean isSuccess() {
    return entries.stream().noneMatch(entry -> entry.getStatus() == Status.ERROR);
  }

  @Override
  public ProgressEntry getLastError() {
    return entries.stream()
        .filter(entry -> entry.getStatus() == Status.ERROR)
        .reduce((first, second) -> second)
        .orElse(null);
  }

  @Override
  public String getExecutor() {
    return executor;
  }

  @Override
  public long determineExecutionDuration() {
    return (System.currentTimeMillis() - startTime) / 1000;
  }
}
