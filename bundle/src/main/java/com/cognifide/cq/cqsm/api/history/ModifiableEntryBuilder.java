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
package com.cognifide.cq.cqsm.api.history;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

@Model(adaptables = Resource.class)
public class ModifiableEntryBuilder {

	public static final String AUTHOR = "author";
	public static final String EXECUTOR_PROPERTY = "executor";
	public static final String EXECUTION_TIME_PROPERTY = "executionTime";
	public static final String FILE_PATH_PROPERTY = "filePath";
	public static final String INSTANCE_HOSTNAME_PROPERTY = "instanceHostname";
	public static final String INSTANCE_TYPE_PROPERTY = "instanceType";
	public static final String MODE = "mode";
	public static final String PROGRESS_LOG_PROPERTY = "summaryJSON";
	private static final Logger LOG = LoggerFactory.getLogger(ModifiableEntryBuilder.class);
	private static final String UPLOAD_TIME = "uploadTime";

	private static final String FILE_NAME = "fileName";

	private final ValueMap valueMap;

	private Resource resource;

	public ModifiableEntryBuilder(Resource resource) {
		this.valueMap = resource.adaptTo(ModifiableValueMap.class);
		this.resource = resource;
	}

	public ModifiableEntryBuilder(ValueMap valueMap) {
		this.valueMap = valueMap;
	}

	public ModifiableEntryBuilder setFileName(String fileName) {
		valueMap.put(FILE_NAME, fileName);
		return this;
	}

	public ModifiableEntryBuilder setFilePath(String filePath) {
		valueMap.put(FILE_PATH_PROPERTY, filePath);
		return this;
	}

	public ModifiableEntryBuilder setMode(String mode) {
		valueMap.put(MODE, mode);
		return this;
	}

	public ModifiableEntryBuilder setExecutor(String executor) {
		valueMap.put(EXECUTOR_PROPERTY, executor);
		return this;
	}

	public ModifiableEntryBuilder setProgressLog(String progressLog) {
		valueMap.put(PROGRESS_LOG_PROPERTY, progressLog);
		return this;
	}

	public ModifiableEntryBuilder setExecutionTime(Calendar executionTime) {
		valueMap.put(EXECUTION_TIME_PROPERTY, executionTime);
		return this;
	}

	public ModifiableEntryBuilder setAuthor(String author) {
		valueMap.put(AUTHOR, author);
		return this;
	}

	public ModifiableEntryBuilder setUploadTime(String uploadTime) {
		valueMap.put(UPLOAD_TIME, uploadTime);
		return this;
	}

	public ModifiableEntryBuilder setInstanceType(String instanceType) {
		valueMap.put(INSTANCE_TYPE_PROPERTY, instanceType);
		return this;
	}

	public ModifiableEntryBuilder setInstanceHostname(String instanceHostname) {
		valueMap.put(INSTANCE_HOSTNAME_PROPERTY, instanceHostname);
		return this;
	}

	public void save() {
		try {
			resource.getResourceResolver().commit();
		} catch (PersistenceException e) {
			LOG.error("Cannot save the resource {} for ModifiableEntry", resource.getPath(), e);
		}
	}
}
