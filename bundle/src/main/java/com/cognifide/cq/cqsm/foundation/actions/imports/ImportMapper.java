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
import com.cognifide.cq.cqsm.api.actions.BasicActionMapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;

import java.util.List;

public final class ImportMapper extends BasicActionMapper {

	@Mapping(
			value = {
					"IMPORT" + SPACE + STRING,
					"IMPORT" + SPACE + QUOTED,
					"INCLUDE" + DASH + "SCRIPT" + SPACE + STRING,
					"INCLUDE" + DASH + "SCRIPT" + SPACE + QUOTED
			},
			args = {"path"},
			reference = "Import script from other file by specifying its path."
	)
	public Action mapAction(final String path) {
		return new Import(path);
	}

	@Mapping(
			value = {
					"IMPORT" + SPACE + LIST,
					"INCLUDE" + DASH + "SCRIPT" + SPACE + LIST
			},
			args = {"path"},
			reference = "Import multiple scripts from other files by specifying its paths."
	)
	public Action mapAction(final List<String> paths) {
		return new Import(paths);
	}
}
