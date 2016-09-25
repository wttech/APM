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

import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;
import com.cognifide.cq.cqsm.core.Cqsm;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;

@Component
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "Resolves the instance type"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
public class InstanceTypeProviderImpl implements InstanceTypeProvider {

	private static final String RUNMODE_AUTHOR = "author";

	@Reference
	private SlingSettingsService settingsService;

	@Override
	public boolean isOnAuthor() {
		return settingsService.getRunModes().contains(RUNMODE_AUTHOR);
	}

}
