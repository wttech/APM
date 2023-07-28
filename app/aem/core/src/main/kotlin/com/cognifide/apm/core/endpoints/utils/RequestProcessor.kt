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
package com.cognifide.apm.core.endpoints.utils

import com.cognifide.apm.core.endpoints.params.RequestParameter
import com.cognifide.apm.core.endpoints.response.ErrorBody
import com.cognifide.apm.core.endpoints.response.JsonObject
import com.cognifide.apm.core.endpoints.response.ResponseEntity
import com.cognifide.apm.core.utils.ServletUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.factory.MissingElementsException
import org.apache.sling.models.factory.ModelFactory
import javax.servlet.http.HttpServletResponse

class RequestProcessor<F>(private val modelFactory: ModelFactory, private val formClass: Class<F>) {

    fun process(httpRequest: SlingHttpServletRequest, httpResponse: SlingHttpServletResponse,
                process: (form: F, resourceResolver: ResourceResolver) -> ResponseEntity<Any>) {
        try {
            val form = modelFactory.createModel(httpRequest, formClass)
            val response = process(form, httpRequest.resourceResolver)

            httpResponse.status = response.statusCode
            ServletUtils.writeJson(httpResponse, body(response.body))
        } catch (e: MissingElementsException) {
            httpResponse.status = HttpServletResponse.SC_BAD_REQUEST
            ServletUtils.writeJson(httpResponse, body(ErrorBody("Bad request", toErrors(e))))
        } catch (e: Exception) {
            httpResponse.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            ServletUtils.writeJson(httpResponse, body(ErrorBody(e.message ?: "")))
        }
    }

    private fun toErrors(e: MissingElementsException) = e.missingElements.mapNotNull { it.element }
        .mapNotNull { it.getAnnotation(RequestParameter::class.java) }
        .map { "Missing required parameter: ${it.value}" }

    private fun body(body: Any) = if (body is JsonObject) body.toMap() else body

}