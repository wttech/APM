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

import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.endpoints.dto.ScriptDto
import com.cognifide.apm.core.utils.ServletUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.servlet.Servlet

@Component(
    service = [Servlet::class],
    property = [
        Property.PATH + "/bin/apm/scripts",
        Property.METHOD + "GET",
        Property.DESCRIPTION + "APM Script List Servlet",
        Property.VENDOR
    ]
)
class ScriptListServlet : SlingAllMethodsServlet() {

    @Reference
    @Transient
    private lateinit var scriptFinder: ScriptFinder

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val scripts = scriptFinder.findAll(request.resourceResolver)
            .map { ScriptDto(it) }
            .sortedBy { it.path }
        ServletUtils.writeJson(response, scripts)
    }
}