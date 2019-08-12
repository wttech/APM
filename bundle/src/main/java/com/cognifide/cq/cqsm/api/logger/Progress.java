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

import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import java.io.Serializable;
import java.util.List;

public interface Progress extends Serializable {

  List<ProgressEntry> getEntries();

  void addEntry(ActionDescriptor descriptor, ActionResult result);

  void addEntry(Status status, String message);

  void addEntry(Status status, List<String> message);

  void addEntry(Status status, String message, String command);

  void addEntry(Status status, List<String> messages, String command);

  boolean isSuccess();

  ProgressEntry getLastError();

  String getExecutor();
}
