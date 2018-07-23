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
package com.cognifide.cq.cqsm.api.actions;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public final class ActionDescriptor {

	private final String command;
	private final List<String> args;
	private final Action action;

	public ActionDescriptor(String command, Action action) {
    this(command, action, Collections.emptyList());
	}

	public ActionDescriptor(String command, Action action, List<String> args) {
		this.command = command;
		this.action = action;
		this.args = args;
	}

  public String argsToString() {
    return args.toString();
  }
}
