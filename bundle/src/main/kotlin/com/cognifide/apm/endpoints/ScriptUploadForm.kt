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

package com.cognifide.apm.endpoints

import com.cognifide.apm.endpoints.utils.RequestParameter
import com.cognifide.cq.cqsm.api.scripts.ExecutionEnvironment
import com.cognifide.cq.cqsm.api.scripts.ExecutionMode
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import java.io.InputStream
import javax.inject.Inject

@Model(adaptables = [SlingHttpServletRequest::class])
class ScriptUploadForm @Inject constructor(
        @param:RequestParameter("file", optional = false) val file: InputStream,
        @param:RequestParameter("file", optional = false, fileName = true) val fileName: String,
        @param:RequestParameter("overwrite") val overwrite: Boolean,
        @param:RequestParameter("cqsm:executionEnabled") val executionEnabled: Boolean,
        @param:RequestParameter("cqsm:executionMode") val executionMode: ExecutionMode,
        @param:RequestParameter("cqsm:executionEnvironment") val executionEnvironment: ExecutionEnvironment?
)