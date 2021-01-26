/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.apm.core.endpoints

import com.cognifide.apm.api.services.ExecutionMode
import com.cognifide.apm.core.endpoints.params.RequestParameter
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import javax.inject.Inject

@Model(adaptables = [SlingHttpServletRequest::class])
class ScriptExecutionForm @Inject constructor(
        @param:RequestParameter("script", optional = false) val script: String,
        @param:RequestParameter("executionMode", optional = false) val executionMode: ExecutionMode,
        @param:RequestParameter("async") val async: Boolean = false,
        @param:RequestParameter("define") val customDefinitions: Map<String, String> = mapOf()
)