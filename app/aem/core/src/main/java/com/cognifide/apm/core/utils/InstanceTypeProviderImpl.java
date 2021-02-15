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
package com.cognifide.apm.core.utils;

import java.util.Set;

import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.cognifide.apm.core.Property;

@Component(
		immediate = true,
		service = InstanceTypeProvider.class,
		property = {
				Property.DESCRIPTION + "Resolves the instance type",
				Property.VENDOR
		}
)
public class InstanceTypeProviderImpl implements InstanceTypeProvider {

	private static final String RUNMODE_AUTHOR = "author";

	@Reference
	private SlingSettingsService settingsService;

	@Override
	public boolean isOnAuthor() {
		return settingsService.getRunModes().contains(RUNMODE_AUTHOR);
	}

	@Override
	public Set<String> getRunModes() {
		return settingsService.getRunModes();
	}

}
