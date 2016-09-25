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
package com.cognifide.cq.cqsm.foundation.actions.check.include;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.utils.AuthorizablesUtils;
import com.cognifide.cq.cqsm.core.actions.ActionUtils;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

public class CheckIncludes implements Action {

	private final List<String> groupIds;

	private final String authorizableId;

	public CheckIncludes(final String id, final List<String> groupIds) {
		this.authorizableId = id;
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

	private ActionResult process(final Context context, boolean execute) {
		ActionResult actionResult = new ActionResult();
		Group authorizable = tryGetGroup(context, actionResult);
		if (authorizable == null) {
			return actionResult;
		}

		List<String> errors = new ArrayList<>();

		boolean checkFailed = checkMembers(context, actionResult, authorizable, errors);

		if (execute && checkFailed) {
			actionResult.logError(ActionUtils.ASSERTION_FAILED_MSG);
			return actionResult;
		}

		ActionUtils.logErrors(errors, actionResult);

		return actionResult;

	}

	private boolean checkMembers(final Context context, final ActionResult actionResult,
			final Group authorizable, final List<String> errors) {
		boolean checkFailed = false;
		for (String id : groupIds) {
			try {
				Authorizable group = AuthorizablesUtils.getAuthorizable(context, id);

				if (!authorizable.isMember(group)) {
					actionResult.logError(id + " is excluded from group " + authorizableId);
					checkFailed = true;
				}
				actionResult.logMessage(id + " is a member of group " + authorizableId);
			} catch (RepositoryException | ActionExecutionException e) {
				errors.add(MessagingUtils.createMessage(e));
			}
		}
		return checkFailed;
	}

	private Group tryGetGroup(final Context context, final ActionResult actionResult) {
		try {
			return AuthorizablesUtils.getGroup(context, authorizableId);
		} catch (RepositoryException | ActionExecutionException e) {
			actionResult.logError(MessagingUtils.createMessage(e));
		}
		return null;
	}

	@Override
	public boolean isGeneric() {
		return true;
	}
}
