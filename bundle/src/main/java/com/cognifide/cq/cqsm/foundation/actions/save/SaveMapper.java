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
package com.cognifide.cq.cqsm.foundation.actions.save;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;

@Mapper("save")
public final class SaveMapper {

	@Mapping(
			reference = "Useful in addition to the SESSION_SAVE ON-DEMAND action to execute saving session.\n" +
					"Ignored when mode other than ON-DEMAND is used."
	)
	public Action mapAction() {
		return new Save();
	}

}
