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
package com.cognifide.apm.core.endpoints

import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.osgi.service.component.annotations.Component
import javax.servlet.Servlet
import com.cognifide.apm.core.history.History
import com.cognifide.apm.core.history.HistoryEntry
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.utils.ServletUtils
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.collections.Predicate
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.osgi.service.component.annotations.Reference

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/history",
            Property.METHOD + "GET",
            Property.DESCRIPTION + "APM History List Servlet",
            Property.VENDOR
        ])
class HistoryListServlet2 : SlingAllMethodsServlet() {

    @Reference
    @Transient
    private lateinit var history: History

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val filter = request.getParameter("filter")
        val executions: List<HistoryEntry> = history.findAllHistoryEntries(request.resourceResolver)
        if (StringUtils.isNotBlank(filter)) {
            CollectionUtils.filter(executions, ExecutionHistoryFilter(filter))
        }
        ServletUtils.writeJson(response, executions)
    }

    inner class ExecutionHistoryFilter(val filterType: String) : Predicate {
        val FILTER_AUTOMATIC_RUN = "automatic run"
        val FILTER_AUTHOR = "author"
        val FILTER_PUBLISH = "publish"


        override fun evaluate(any: Any): Boolean {
            val executionModel: HistoryEntry = any as HistoryEntry
            var value: String?
            value = when (filterType) {
                FILTER_AUTHOR -> executionModel.instanceType
                FILTER_PUBLISH -> executionModel.instanceType
                FILTER_AUTOMATIC_RUN -> executionModel.executor
                else -> null
            }
            return filterType == value
        }
    }
}