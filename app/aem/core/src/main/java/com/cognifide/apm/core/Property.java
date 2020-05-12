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
package com.cognifide.apm.core;

import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;

public class Property {

	private Property() {
		// util constructor
	}

	public static final String PATH = ServletResolverConstants.SLING_SERVLET_PATHS + "=";

	public static final String METHOD = ServletResolverConstants.SLING_SERVLET_METHODS + "=";

	public static final String RESOURCE_TYPE = ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=";

	public static final String SELECTOR = ServletResolverConstants.SLING_SERVLET_SELECTORS + "=";

	public static final String EXTENSION = ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=";

	public static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION + "=";

	public static final String TOPIC = JobConsumer.PROPERTY_TOPICS + "=";

	public static final String SCHEDULER = "scheduler.expression=";

	public static final String RESOURCE_PATH = ResourceChangeListener.PATHS + "=";

	public static final String CHANGE_TYPE = ResourceChangeListener.CHANGES + "=";

	public static final String VENDOR = Constants.SERVICE_VENDOR + "=Cognifide";

}
