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
import com.cognifide.cq.cqsm.api.actions.BasicActionMapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;

public final class SetPropertyMapper extends BasicActionMapper {

	@Mapping(
			value = {
					"SET" + DASH + "PROPERTY" + SPACE + STRING + SPACE + QUOTED,
					"SET" + DASH + "PROPERTY" + SPACE + STRING + SPACE + STRING
			},
			args = {"name", "value"},
			reference = "This is general purpose action which can be used to assign specified value to the specified"
					+ " property."
	)
	public Action mapAction(final String name, final String value) {
		return new SetProperty(name, value);
	}

}
