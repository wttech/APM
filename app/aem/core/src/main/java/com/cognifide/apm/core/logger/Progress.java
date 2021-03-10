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

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.api.status.Status;
import com.cognifide.apm.core.actions.ActionDescriptor;
import com.cognifide.apm.core.grammar.argument.Arguments;
import java.io.Serializable;
import java.util.List;

public interface Progress extends Serializable, ExecutionResult {

  void addEntry(ActionDescriptor descriptor, ActionResult result);

  void addEntry(Status status, String message);

  void addEntry(Status status, List<String> message);

  void addEntry(Status status, String message, String command);

  void addEntry(Status status, List<String> messages, String command);

  void addEntry(Status status, List<String> messages, String command, String authorizable, Arguments arguments,
      Position position);

}
