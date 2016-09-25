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
package com.cognifide.cq.cqsm.foundation.actions.allow;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.BasicActionMapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;

import java.util.Collections;
import java.util.List;

public class AllowMapper extends BasicActionMapper {

	public static final String REFERENCE = "Add allow permissions for current authorizable on specified path.";

	@Mapping(
			value = {"ALLOW" + SPACE + PATH + SPACE + LIST},
			args = {"path", "permissions"},
			reference = REFERENCE
	)
	public Action mapAction(String path, List<String> permissions) throws ActionCreationException {
		return mapAction(path, null, permissions, false);
	}

	@Mapping(
			value = {"ALLOW" + SPACE + PATH + SPACE + STRING},
			args = {"path", "permission"},
			reference = REFERENCE
	)
	public Action mapAction(String path, String permission) throws ActionCreationException {
		return mapAction(path, null, Collections.singletonList(permission), false);
	}

	@Mapping(
			value = {"ALLOW" + SPACE + PATH + SPACE + LIST + SPACE + ("IF" + DASH + "EXISTS")},
			args = {"path", "permissions"},
			reference = REFERENCE
	)
	public Action mapActionWithIfExists(String path, List<String> permissions) throws ActionCreationException {
		return mapAction(path, null, permissions, true);
	}

	@Mapping(
			value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + LIST},
			args = {"path", "glob", "permissions"},
			reference = REFERENCE
	)
	public Action mapAction(String path, String glob, List<String> permissions)
			throws ActionCreationException {
		return mapAction(path, glob, permissions, false);
	}

	@Mapping(
			value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + STRING},
			args = {"path", "glob", "permission"},
			reference = REFERENCE
	)
	public Action mapAction(String path, String glob, String permission)
			throws ActionCreationException {
		return mapAction(path, glob, Collections.singletonList(permission), false);
	}

	@Mapping(
			value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + LIST + SPACE + ("IF" + DASH + "EXISTS")},
			args = {"path", "glob", "permissions"},
			reference = REFERENCE
	)
	public Action mapActionWithIfExists(String path, String glob, List<String> permissions) throws ActionCreationException {
		return mapAction(path, glob, permissions, true);
	}

	private Action mapAction(String path, String glob, List<String> permissions, Boolean ifExists)
			throws ActionCreationException {
		return new Allow(path, glob, ifExists, permissions);
	}
}
