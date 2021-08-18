/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
package com.cognifide.apm.core.scriptrunnerjob;

import java.util.List;

import com.cognifide.apm.api.services.ExecutionResult.Entry;
import com.cognifide.apm.core.jobs.ScriptRunnerJobStatus;

import lombok.Getter;

/**
 * @deprecated use {@link com.cognifide.apm.core.services.async.ExecutionStatus} instead
 */
@Deprecated
public class JobProgressOutput {

	@Getter
	private final String type;

	@Getter
	private String path;

	@Getter
	private List<Entry> entries;

	public JobProgressOutput(ScriptRunnerJobStatus status) {
		this.type = status.toString();
	}

	public JobProgressOutput(ScriptRunnerJobStatus status, String path, List<Entry> entries) {
		this.entries = entries;
		this.path = path;
		this.type = status.toString();
	}
}
