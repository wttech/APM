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
package com.cognifide.apm.endpoints

import com.cognifide.apm.endpoints.utils.AbstractFormServlet
import com.cognifide.apm.endpoints.utils.ResponseEntity
import com.cognifide.apm.endpoints.utils.badRequest
import com.cognifide.apm.endpoints.utils.ok
import com.cognifide.apm.api.services.Mode
import com.cognifide.cq.cqsm.api.logger.ProgressEntry
import com.cognifide.cq.cqsm.api.logger.Status
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.cq.cqsm.api.logger.ExecutionResult
import com.cognifide.cq.cqsm.core.Property
import com.cognifide.cq.cqsm.core.scripts.ScriptStorageException
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.factory.ModelFactory
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.servlet.Servlet

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/script/validate",
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
            val result = scriptManager.evaluate(form.path, form.content, Mode.VALIDATION, resourceResolver)
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

    private fun transformToErrors(entry: ProgressEntry): List<String> {
        val positionPrefix = positionPrefix(entry)
        return entry.messages.map { message -> positionPrefix + message }
    }

    private fun positionPrefix(progressEntry: ProgressEntry): String {
        val position = progressEntry.position
        return if (position != null) "Invalid line ${position.line}: " else ""
    }
}