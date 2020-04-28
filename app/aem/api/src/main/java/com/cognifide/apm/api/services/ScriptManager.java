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
package com.cognifide.apm.api.services;

import com.cognifide.apm.api.scripts.Script;
import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

public interface ScriptManager {

  String FILE_FOR_EVALUATION = "evaluation.apm";

  /**
   * Fail-safe execution of script in concrete mode (dry run, automatic execution, validation)
   */
  ExecutionResult process(Script script, ExecutionMode mode, ResourceResolver resolver)
      throws RepositoryException, PersistenceException;


  /**
   * Fail-safe execution of script in concrete mode (dry run, automatic execution, validation)
	 */
	ExecutionResult process(Script script, ExecutionMode mode, Map<String, String> customDefinitions, ResourceResolver resolver)
			throws RepositoryException, PersistenceException;

	/**
	 * Fail-safe script content execution in concrete mode
	 */
	ExecutionResult evaluate(String path, String content, ExecutionMode mode, ResourceResolver resolver)
			throws RepositoryException, PersistenceException;

	/**
	 * Fail-safe script content execution in concrete mode
	 */
	ExecutionResult evaluate(String path, String content, ExecutionMode mode, Map<String, String> customDefinitions,
			ResourceResolver resolver)
			throws RepositoryException, PersistenceException;

	/**
	 * Get predefined variables accessible in script via syntax: ${definitionName}
	 */
	Map<String, String> getPredefinedDefinitions();
}
