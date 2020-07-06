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

import com.cognifide.apm.api.scripts.LaunchEnvironment
import com.cognifide.apm.api.scripts.LaunchMode
import com.cognifide.apm.core.endpoints.params.DateFormat
import com.cognifide.apm.core.endpoints.params.FileName
import com.cognifide.apm.core.endpoints.params.RequestParameter
import com.cognifide.apm.core.scripts.LaunchMetadata
import com.cognifide.apm.core.scripts.ScriptNode
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import java.io.InputStream
import java.time.LocalDateTime
import javax.inject.Inject

@Model(adaptables = [SlingHttpServletRequest::class])
class ScriptUploadForm @Inject constructor(
        @param:RequestParameter("file", optional = false) val file: InputStream,
        @param:RequestParameter("file", optional = false) @param:FileName val fileName: String,
        @param:RequestParameter("overwrite") val overwrite: Boolean,
        @param:RequestParameter(ScriptNode.APM_LAUNCH_ENABLED) val launchEnabled: Boolean,
        @param:RequestParameter(ScriptNode.APM_LAUNCH_MODE) val launchMode: LaunchMode,
        @param:RequestParameter(ScriptNode.APM_LAUNCH_ENVIRONMENT) val launchEnvironment: LaunchEnvironment?,
        @param:RequestParameter(ScriptNode.APM_LAUNCH_HOOK) val launchHook: String?,
        @param:RequestParameter(ScriptNode.APM_LAUNCH_SCHEDULE) @param:DateFormat("yyyy-MM-dd'T'HH:mm:ss") val launchSchedule: LocalDateTime?
) {
    fun toLaunchMetadata(): LaunchMetadata {
        return LaunchMetadata(launchEnabled, launchMode, launchEnvironment, launchHook, launchSchedule)
    }
}