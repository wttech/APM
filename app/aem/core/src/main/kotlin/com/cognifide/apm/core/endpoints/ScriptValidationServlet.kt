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
package com.cognifide.apm.core.endpoints

import com.cognifide.apm.api.scripts.TransientScript
import com.cognifide.apm.api.services.ExecutionMode
import com.cognifide.apm.api.services.ExecutionResult
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.apm.api.status.Status
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.endpoints.response.ResponseEntity
import com.cognifide.apm.core.endpoints.response.badRequest
import com.cognifide.apm.core.endpoints.response.ok
import com.cognifide.apm.core.logger.ProgressEntry
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
            Property.PATH + "/bin/apm/scripts/validate",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Script Validation Servlet",
            Property.VENDOR
        ])
class ScriptValidationServlet : AbstractFormServlet<ScriptValidationForm>(ScriptValidationForm::class.java) {

    @Reference
    @Transient
    private lateinit var scriptManager: ScriptManager

    @Reference
    override fun setup(modelFactory: ModelFactory) {
        this.modelFactory = modelFactory
    }

    override fun doPost(form: ScriptValidationForm, resourceResolver: ResourceResolver): ResponseEntity<Any> {
        return try {
            val script = TransientScript.create(form.path, form.content);
            val result = scriptManager.process(script, ExecutionMode.VALIDATION, resourceResolver)
            if (result.isSuccess) {
                ok {
                    message = "Script passes validation"
                    "valid" set true
                }
            } else {
                val validationErrors = transformToValidationErrors(result)
                ok {
                    message = "Script does not pass validation"
                    "valid" set false
                    "errors" set validationErrors
                }
            }
        } catch (e: ScriptStorageException) {
            badRequest {
                message = e.message ?: "Errors while saving script"
                errors = e.errors
            }
        }
    }

    private fun transformToValidationErrors(result: ExecutionResult): List<String> {
        return result.entries.filter { it.status == Status.ERROR }
                .filter { it.messages.isNotEmpty() }
                .flatMap { transformToErrors(it) }
    }

    private fun transformToErrors(entry: ExecutionResult.Entry): List<String> {
        val positionPrefix = positionPrefix(entry)
        return entry.messages.map { message -> positionPrefix + message }
    }

    private fun positionPrefix(entry: ExecutionResult.Entry): String {
        val position = if (entry is ProgressEntry) entry.position else null
        return if (position != null) "Invalid line ${position.line}: " else ""
    }
}