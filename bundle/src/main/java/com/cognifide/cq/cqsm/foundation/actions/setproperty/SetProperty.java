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
package com.cognifide.cq.cqsm.foundation.actions.setproperty;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Value;

public class SetProperty implements Action {

	private static final Logger LOGGER = LoggerFactory.getLogger(SetProperty.class);

	private final String nameProperty;

	private final String valueProperty;

	public SetProperty(final String nameProperty, final String valueProperty) {
		this.nameProperty = nameProperty;
		this.valueProperty = valueProperty;
	}

	@Override
	public ActionResult simulate(final Context context) {
		return process(context, true);
	}

	@Override
	public ActionResult execute(final Context context) {
		return process(context, false);
	}

	private ActionResult process(final Context context, boolean simulate) {
		ActionResult actionResult = new ActionResult();

		try {
			Authorizable authorizable = context.getCurrentAuthorizable();
			actionResult.setAuthorizable(authorizable.getID());
			LOGGER.info(String.format("Setting property %s for authorizable with id = %s", nameProperty,
					authorizable.getID()));
			final Value value = context.getValueFactory().createValue(valueProperty);

			if (!simulate) {
				authorizable.setProperty(nameProperty, value);
			}

			actionResult.logMessage(
					"Property " + nameProperty + " for " + authorizable.getID() + " added vith value: "
							+ valueProperty);
		} catch (RepositoryException | ActionExecutionException e) {
			actionResult.logError(MessagingUtils.createMessage(e));
		}
		return actionResult;
	}

	@Override
	public boolean isGeneric() {
		return false;
	}

}
