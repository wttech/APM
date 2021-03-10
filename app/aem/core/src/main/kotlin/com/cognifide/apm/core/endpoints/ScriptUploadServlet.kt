/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.endpoints.dto.ScriptDto
import com.cognifide.apm.core.endpoints.response.ResponseEntity
import com.cognifide.apm.core.endpoints.response.badRequest
import com.cognifide.apm.core.endpoints.response.ok
import com.cognifide.apm.core.scripts.ScriptStorage
import com.cognifide.apm.core.scripts.ScriptStorageException
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.factory.ModelFactory
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.servlet.Servlet

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/scripts/upload",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Script Upload Servlet",
            Property.VENDOR
        ])
class ScriptUploadServlet : AbstractFormServlet<ScriptUploadForm>(ScriptUploadForm::class.java) {

    @Reference
    @Transient
    private lateinit var scriptStorage: ScriptStorage

    @Reference
    @Transient
    private lateinit var scriptManager: ScriptManager

    @Reference
    override fun setup(modelFactory: ModelFactory) {
        this.modelFactory = modelFactory
    }

    override fun doPost(form: ScriptUploadForm, resourceResolver: ResourceResolver): ResponseEntity<Any> {
        return try {
            val script = scriptStorage.save(form.savePath, form.fileName, form.file, form.toLaunchMetadata(), form.overwrite, resourceResolver)
            scriptManager.process(script, ExecutionMode.VALIDATION, resourceResolver)
            ok {
                message = "File successfully saved"
                "uploadedScript" set ScriptDto(script)
            }
        } catch (e: ScriptStorageException) {
            badRequest {
                message = e.message ?: "Errors while saving script"
                errors = e.errors
            }
        }
    }
}