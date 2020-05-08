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

import com.cognifide.apm.core.endpoints.utils.AbstractFormServlet
import com.cognifide.apm.core.endpoints.utils.ResponseEntity
import com.cognifide.apm.core.endpoints.utils.badRequest
import com.cognifide.apm.core.endpoints.utils.ok
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage
import com.cognifide.cq.cqsm.core.Property
import com.cognifide.cq.cqsm.core.scripts.ScriptStorageException
import com.cognifide.cq.cqsm.core.scripts.ScriptUtils
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.factory.ModelFactory
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.servlet.Servlet

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/script/upload",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Script Upload Servlet",
            Property.VENDOR
        ])
class ScriptUploadServlet : AbstractFormServlet<ScriptUploadForm>(ScriptUploadForm::class.java) {

    @Reference
    @Transient
    private lateinit var scriptStorage: ScriptStorage

    @Reference
    override fun setup(modelFactory: ModelFactory) {
        this.modelFactory = modelFactory
    }

    override fun doPost(form: ScriptUploadForm, resourceResolver: ResourceResolver): ResponseEntity<Any> {
        return try {
            val script = scriptStorage.save(form.fileName, form.file, form.toLaunchMetadata(), form.overwrite, resourceResolver)
            ok {
                message = "File successfully saved"
                "uploadedScript" set ScriptUtils.asMap(script)
            }
        } catch (e: ScriptStorageException) {
            badRequest {
                message = e.message ?: "Errors while saving script"
                errors = e.errors
            }
        }
    }
}