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
package com.cognifide.cq.cqsm.foundation.actions.define;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.actions.interfaces.DefinitionProvider;
import com.cognifide.cq.cqsm.api.executors.Context;

import java.util.Collections;
import java.util.Map;

public class Define implements Action, DefinitionProvider {

	private final String name;

	private final String value;

	private final boolean ignoreIfExists;

	public Define(String name, String value, boolean ignoreIfExists) {
		this.name = name;
		this.value = value;
		this.ignoreIfExists = ignoreIfExists;
	}

	@Override
	public ActionResult simulate(Context context) {
		return execute(context);
	}

	@Override
	public ActionResult execute(Context context) {
		ActionResult actionResult = new ActionResult();
		actionResult.logMessage("Definition saved");

		return actionResult;
	}

	@Override
	public boolean isGeneric() {
		return false;
	}

	@Override
	public Map<String, String> provideDefinitions(Map<String, String> alreadyDefined) {
		Map<String, String> result = Collections.emptyMap();
		boolean definitionExists = alreadyDefined.containsKey(name);
		boolean shouldBeAdded = !(definitionExists && ignoreIfExists);
		if (shouldBeAdded) {
			result = Collections.singletonMap(name, value);
		}
		return result;
	}
}
