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
package com.cognifide.cq.cqsm.core.servlets.run;

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.core.models.ScriptsRowModel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.event.jobs.Job;

public class ScriptRunParameters {

	public static final String JOB_SCRIPT_RUN_TOPIC = "script/job/run";
	public static final String SCRIPT_PATH_PROPERTY_NAME = "file";
	public static final String MODE_NAME_PROPERTY_NAME = "mode";
	public static final String USER_NAME_PROPERTY_NAME = "userName";

	private String searchPath;

	private String modeName;

	private String userName;

	public ScriptRunParameters(String searchPath, String modeName, String userName) {
		this.searchPath = searchPath;
		this.modeName = modeName;
		this.userName = userName;
	}

	public String getSearchPath() {
		return searchPath;
	}

	public String getModeName() {
		return modeName;
	}

	public String getUserName() {
		return userName;
	}

	public boolean areValid() {
		return true;
		// TODO
	}

	public Map<String, Object> asMap() {
		final Map<String, Object> props = new HashMap<>();
		props.put(SCRIPT_PATH_PROPERTY_NAME, this.getSearchPath());
		props.put(MODE_NAME_PROPERTY_NAME, this.getModeName());
		props.put(USER_NAME_PROPERTY_NAME, this.getUserName());
		return props;
	}
}
