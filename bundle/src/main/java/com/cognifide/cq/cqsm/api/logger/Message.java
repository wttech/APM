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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Message {

	public static final String ERROR = "error";

	public static final String WARNING = "warning";

	public static final String INFO = "info";

	@Getter
	private String text;

	@Getter
	private String type;

	// Gson library needs this
	private Message() {
	}

	public static Message getErrorMessage(String text) {
		return new Message(text, ERROR);
	}

	public static Message getWarningMessage(String text) {
		return new Message(text, WARNING);
	}

	public static Message getInfoMessage(String text) {
		return new Message(text, INFO);
	}
}
