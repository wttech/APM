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
package com.cognifide.apm.endpoints.utils

import com.cognifide.cq.cqsm.core.utils.ServletUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.apache.sling.models.factory.ModelFactory
import org.osgi.service.component.annotations.Reference
import java.io.IOException
import javax.servlet.http.HttpServletResponse

abstract class AbstractFormServlet<F>(private val formClass: Class<F>) : SlingAllMethodsServlet() {

    @Reference
    @Transient
    protected lateinit var modelFactory: ModelFactory

    @Throws(IOException::class)
    override fun doPost(httpRequest: SlingHttpServletRequest, httpResponse: SlingHttpServletResponse) {
        try {
            val form = modelFactory.createModel(httpRequest, formClass)
            val response = doPost(form, httpRequest.resourceResolver)

            httpResponse.setStatus(response.statusCode)
            ServletUtils.writeJson(httpResponse, body(response.body))
        } catch (e: Exception) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            ServletUtils.writeJson(httpResponse, body(ErrorBody("Cannot save script in repository: " + e.message)))
        }
    }

    private fun body(body: Any) = if (body is JsonObject) body.toMap() else body

    abstract fun setup(modelFactory: ModelFactory)

    abstract fun doPost(form: F, resourceResolver: ResourceResolver): ResponseEntity<Any>
}