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

import com.cognifide.cq.cqsm.api.logger.Message;
import com.cognifide.cq.cqsm.api.logger.Status;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class ActionResult {

	@Getter
	protected String authorizable;

	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Getter
	protected List<Message> messages;

	@Getter
	protected Status status;

	public ActionResult(String authorizable) {
		this();
		this.authorizable = authorizable;
	}

	public ActionResult() {
		this.messages = new ArrayList<>();
		this.status = Status.SUCCESS;
	}

	public void setAuthorizable(String authorizable) {
		if (this.authorizable == null) {
			this.authorizable = authorizable;
		}
	}

	public void logMessage(final String message) {
		messages.add(Message.getInfoMessage(message));
	}

	public void logWarning(final String warning) {
		messages.add(Message.getWarningMessage(warning));
		if (status != Status.ERROR) {
			status = Status.WARNING;
		}
	}

	public void logError(final String error) {
		messages.add(Message.getErrorMessage(error));
		status = Status.ERROR;
	}

	/**
	 * Used for copying error messages from other ActionResult object, when action is called within other
	 * action, as they do not throw exceptions
	 *
	 * @param result the ActionResult object to copy messages from
	 */
	public void logError(final ActionResult result) {
		for (Message msg : result.getMessages()) {
			if (Message.ERROR.equals(msg.getType())) {
				logWarning(msg.getText() + ", continuing");
			}
		}

	}

}
