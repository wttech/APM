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

import java.util.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

public class ProgressEntry {

	@Getter
	private String authorizable;

	@Getter
	private String actionName;

	@Getter
	private String command;

	@Getter
	private final List<Message> messages;

	@Getter
	private final Status status;

	@Getter
	private String parameters;

	public ProgressEntry(ActionDescriptor actionDescriptor, ActionResult actionResult) {
		this.actionName = actionDescriptor.getAction().getClass().getSimpleName();
		this.command = actionDescriptor.getCommand();
		this.parameters = StringUtils.join(actionDescriptor.getArgs(), " ");
		this.authorizable = actionResult.getAuthorizable();
		this.messages = new LinkedList<>(actionResult.getMessages());
		this.status = actionResult.getStatus();
	}

	public ProgressEntry(Message message, Status status) {
		this.messages = new LinkedList<>();
		this.messages.add(message);
		this.status = status;
	}

	public ProgressEntry(String command, Message message, Status status) {
		this.authorizable = "";
		this.actionName = "";
		this.parameters = "";
		this.command = command;
		this.messages = Collections.singletonList(message);
		this.status = status;
	}

	public String getLastMessageText() {
		String message = null;
		if (CollectionUtils.isNotEmpty(messages)) {
			message = messages.get(messages.size() - 1).getText();
		}

		return message;
	}
}
