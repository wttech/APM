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

import com.cognifide.cq.cqsm.api.exceptions.ExecutionException;
import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

public interface ScriptManager {

	String FILE_FOR_EVALUATION = "evaluation.cqsm";

	/**
	 * Fail-safe execution of script in concrete mode (dry run, automatic execution, validation)
	 */
	Progress process(final Script script, final Mode mode, ResourceResolver resolver)
			throws RepositoryException, PersistenceException;

	/**
	 * Fail-safe script content execution in concrete mode
	 */
	Progress evaluate(String scriptContent, Mode mode, ResourceResolver resolver)
			throws RepositoryException, PersistenceException;

	/**
	 * Get service for monitoring script events
	 */
	EventManager getEventManager();

	/**
	 * Get predefined variables accessible in script via syntax: ${definitionName}
	 */
	Map<String, String> getPredefinedDefinitions();

	/**
	 * Find all nested scripts in specified
	 */
	List<Script> findIncludes(Script script, ResourceResolver resolver) throws ExecutionException;
}
