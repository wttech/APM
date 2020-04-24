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
package com.cognifide.cq.cqsm.api.scripts;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.api.services.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;

public interface EventListener {

	/**
	 * Handle event trigerred by {@link ScriptManager}
	 * If event is not related with execution only first argument is specified, other are nulls
	 */
	void handle(Script script, Mode mode, Progress progress);
}
