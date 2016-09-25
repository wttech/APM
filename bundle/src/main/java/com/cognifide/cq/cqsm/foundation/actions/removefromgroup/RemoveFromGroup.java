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
package com.cognifide.cq.cqsm.foundation.actions.removefromgroup;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.utils.AuthorizablesUtils;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

public class RemoveFromGroup implements Action {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFromGroup.class);

	private final List<String> groupIds;

	public RemoveFromGroup(final List<String> groupIds) {
		this.groupIds = groupIds;
	}

	@Override
	public ActionResult simulate(Context context) {
		return process(context, false);
	}

	@Override
	public ActionResult execute(final Context context) {
		return process(context, true);
	}

	public ActionResult process(final Context context, boolean execute) {
		ActionResult actionResult = new ActionResult();
		List<String> errors = new ArrayList<>();
		Authorizable authorizable = null;
		try {
			authorizable = context.getCurrentAuthorizable();
			actionResult.setAuthorizable(authorizable.getID());
			LOGGER.info(
					String.format("Removing authorizable with id = %s from groups %s", authorizable.getID(),
							groupIds));
		} catch (ActionExecutionException e) {
			actionResult.logError(MessagingUtils.createMessage(e));
			return actionResult;
		} catch (RepositoryException e) {
			actionResult.logError(MessagingUtils.createMessage(e));
			return actionResult;
		}

		for (String id : groupIds) {
			try {
				Group group = AuthorizablesUtils.getGroup(context, id);

				if (execute) {
					group.removeMember(authorizable);
				}

				actionResult.logMessage(MessagingUtils.removedFromGroup(authorizable.getID(), id));
			} catch (RepositoryException | ActionExecutionException e) {
				errors.add(MessagingUtils.createMessage(e));
			}
		}

		if (!errors.isEmpty()) {
			for (String error : errors) {
				actionResult.logError(error);
			}
			actionResult.logError("Execution interrupted");
		}
		return actionResult;
	}

	@Override
	public boolean isGeneric() {
		return false;
	}
}
