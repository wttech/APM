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
package com.cognifide.cq.cqsm.api.scripts;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

public interface ScriptStorage {

	/**
	 * Get scripts path depending on instance type
	 */
	String getSavePath();

	/**
	 * Remove script
	 */
	void remove(Script script, ResourceResolver resolver) throws RepositoryException;

	/**
	 * Save script, for example from upload
	 */
	Script save(String fileName, InputStream input, boolean overwrite, ResourceResolver resolver)
			throws RepositoryException, PersistenceException;

	/**
	 * Batch save multiple scripts at once (with some 'include' dependencies between them)
	 * Map definition: key - should be a file name, value - file content in CQSM language
	 */
	List<Script> saveAll(Map<String, InputStream> files, boolean overwrite, ResourceResolver resolver)
			throws RepositoryException, PersistenceException;

}
