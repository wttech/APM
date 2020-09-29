package com.cognifide.apm.core.endpoints

import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.grammar.ReferenceFinder
import com.cognifide.apm.core.utils.ServletUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.servlet.Servlet

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/graph",
            Property.METHOD + "GET",
            Property.DESCRIPTION + "APM Scripts Graph Servlet",
            Property.VENDOR
        ])
class ScriptsGraphServlet : SlingAllMethodsServlet(){

    @Reference
    @Transient
    lateinit var scriptFinder: ScriptFinder

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val all = scriptFinder.findAll(request.resourceResolver)
        val referenceFinder = ReferenceFinder(scriptFinder, request.resourceResolver)
        val referenceGraph = referenceFinder.getReferenceGraph(*all.toTypedArray())

        ServletUtils.writeJson(response, GraphDto(referenceGraph))
    }
}