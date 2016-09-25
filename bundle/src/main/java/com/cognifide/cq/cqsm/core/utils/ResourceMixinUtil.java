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
package com.cognifide.cq.cqsm.core.utils;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ResourceMixinUtil {

	private ResourceMixinUtil() {
		// empty constructor
	}

	public static void addMixin(ModifiableValueMap vm, String mixin) {
		Set<String> mixins = new HashSet<>(Arrays.asList(vm.get(JcrConstants.JCR_MIXINTYPES, new String[0])));
		mixins.add(mixin);
		vm.put(JcrConstants.JCR_MIXINTYPES, mixins.toArray(new String[]{}));
	}

}
