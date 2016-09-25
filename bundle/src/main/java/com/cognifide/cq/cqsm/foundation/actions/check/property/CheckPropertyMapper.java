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
package com.cognifide.cq.cqsm.foundation.actions.check.property;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.BasicActionMapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;

public final class CheckPropertyMapper extends BasicActionMapper {

	public static final String REFERENCE = "Verify that a property is set to specific value for given authorizable.";

	@Mapping(
			value = {
					"CHECK" + DASH + "PROPERTY" + SPACE + STRING + SPACE + STRING + SPACE + QUOTED,
					"CHECK" + DASH + "PROPERTY" + SPACE + STRING + SPACE + STRING + SPACE + STRING
			},
			args = {"authorizableId", "name", "value"},
			reference = REFERENCE
	)
	public Action mapAction(final String id, final String name, final String value) {
		return new CheckProperty(id, name, value);
	}
}
