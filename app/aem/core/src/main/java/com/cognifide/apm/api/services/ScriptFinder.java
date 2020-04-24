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
import java.util.List;
import java.util.function.Predicate;
import org.apache.sling.api.resource.ResourceResolver;

public interface ScriptFinder {

	/**
	 * Find script by relative or absolute path
	 */
	Script find(String path, ResourceResolver resolver);

	/**
	 * Find script by relative or absolute path
	 */
	Script find(String path, boolean skipIgnored, ResourceResolver resolver);

	/**
	 * Find all available scripts
	 */
	List<Script> findAll(ResourceResolver resolver);

	/**
	 * Find scripts that matched filter
	 */
	List<Script> findAll(Predicate<Script> filter, ResourceResolver resolver);

}
