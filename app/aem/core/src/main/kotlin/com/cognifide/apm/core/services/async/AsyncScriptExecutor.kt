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
package com.cognifide.apm.core.services.async

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ExecutionMode

interface AsyncScriptExecutor {

    fun process(
        script: Script, executionMode: ExecutionMode, customDefinitions: Map<String, String>, executor: String
    ): String

    fun checkStatus(id: String): ExecutionStatus
}