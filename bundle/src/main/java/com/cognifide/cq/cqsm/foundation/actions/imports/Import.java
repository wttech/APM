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
package com.cognifide.cq.cqsm.foundation.actions.imports;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.actions.interfaces.ScriptProvider;
import com.cognifide.cq.cqsm.api.executors.Context;

import java.util.Collections;
import java.util.List;

public class Import implements Action, ScriptProvider {

	private final List<String> paths;

	public Import(String path) {
		this(Collections.singletonList(path));
	}

	public Import(List<String> paths) {
		this.paths = paths;
	}

	@Override
	public ActionResult simulate(Context context) {
		return execute(context);
	}

	@Override
	public ActionResult execute(Context context) {
		ActionResult actionResult = new ActionResult();
		actionResult.logMessage("Script included properly");

		return actionResult;
	}

	@Override
	public boolean isGeneric() {
		return false;
	}

	@Override
	public List<String> provideScripts() {
		return paths;
	}
}
