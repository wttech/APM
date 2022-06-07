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

import com.cognifide.apm.core.endpoints.response.ResponseEntity
import com.cognifide.apm.core.endpoints.utils.RequestProcessor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.apache.sling.models.factory.ModelFactory
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferencePolicyOption
import java.io.IOException

abstract class AbstractFormServlet<F>(private val formClass: Class<F>) : SlingAllMethodsServlet() {

    @Reference(policyOption = ReferencePolicyOption.GREEDY)
    @Transient
    protected lateinit var modelFactory: ModelFactory

    @Throws(IOException::class)
    override fun doPost(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        RequestProcessor(modelFactory, formClass).process(request, response) { form, resourceResolver -> doPost(form, resourceResolver) }
    }

    abstract fun setup(modelFactory: ModelFactory)

    abstract fun doPost(form: F, resourceResolver: ResourceResolver): ResponseEntity<Any>
}