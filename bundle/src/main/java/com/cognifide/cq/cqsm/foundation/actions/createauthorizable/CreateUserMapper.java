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
package com.cognifide.cq.cqsm.foundation.actions.createauthorizable;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.BasicActionMapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;

public class CreateUserMapper extends BasicActionMapper {

	public static final String REFERENCE = "Create a user.";

	@Mapping(
			value = {"CREATE" + DASH + "USER" + SPACE + STRING},
			args = {"userId"},
			reference = REFERENCE
	)
	public Action mapAction(String id) {
		return mapAction(id, null, null, false);
	}

	@Mapping(
			value = {"CREATE" + DASH + "USER" + SPACE + STRING + SPACE + STRING},
			args = {"userId", "password"},
			reference = REFERENCE
	)
	public Action mapAction(String id, String password) {
		return mapAction(id, password, null, false);
	}

	@Mapping(
			value = {"CREATE" + DASH + "USER" + SPACE + STRING + SPACE + HOME_PATH},
			args = {"userId", "path"},
			reference = REFERENCE
	)
	public Action mapActionWithPath(String id, String path) {
		return mapAction(id, null, path, false);
	}

	@Mapping(
			value = {"CREATE" + DASH + "USER" + SPACE + STRING + SPACE + ("IF" + DASH + "NOT" + DASH + "EXISTS")},
			args = {"userId"},
			reference = REFERENCE
	)
	public Action mapActionWithIfNotExists(String id) {
		return mapAction(id, null, null, true);
	}

	@Mapping(
			value = {"CREATE" + DASH + "USER" + SPACE + STRING + SPACE + STRING + SPACE + ("IF" + DASH + "NOT" + DASH + "EXISTS")},
			args = {"userId", "password"},
			reference = REFERENCE
	)
	public Action mapActionWithIfNotExists(String id, String password) {
		return mapAction(id, password, null, true);
	}

	@Mapping(
			value = {"CREATE" + DASH + "USER" + SPACE + STRING + SPACE + HOME_PATH + SPACE + ("IF" + DASH + "NOT" + DASH + "EXISTS")},
			args = {"userId", "path"},
			reference = REFERENCE
	)
	public Action mapActionWithPathAndIfNotExists(String id, String path) {
		return mapAction(id, null, path, true);
	}

	@Mapping(
			value = {"CREATE" + DASH + "USER" + SPACE + STRING + SPACE + STRING + SPACE + HOME_PATH},
			args = {"userId", "password", "path"},
			reference = REFERENCE
	)
	public Action mapAction(String id, String password, String path) {
		return mapAction(id, password, path, false);
	}

	@Mapping(
			value = {"CREATE" + DASH + "USER" + SPACE + STRING + SPACE + STRING + SPACE + HOME_PATH + SPACE + ("IF" + DASH + "NOT" + DASH + "EXISTS")},
			args = {"userId", "password", "path"},
			reference = REFERENCE
	)
	public Action mapActionWithIfNotExists(String id, String password, String path) {
		return mapAction(id, password, path, true);
	}

	private Action mapAction(String id, String password, String path, Boolean ifNotExists) {
		return new CreateAuthorizable(id, password, path, ifNotExists, CreateAuthorizableStrategy.USER);
	}

}
