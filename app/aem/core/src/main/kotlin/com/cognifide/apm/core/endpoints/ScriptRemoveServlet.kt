package com.cognifide.apm.core.endpoints

import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.servlet.Servlet
import com.cognifide.apm.core.scripts.ScriptStorage
import com.cognifide.apm.api.services.ScriptFinder
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import javax.servlet.http.HttpServletResponse
import com.cognifide.apm.core.utils.ServletUtils
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.resource.ResourceResolver
import javax.jcr.RepositoryException
import com.cognifide.apm.core.Property

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/remove",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Remove Scripts Servlet",
            Property.VENDOR
        ])

class ScriptRemoveServlet : SlingAllMethodsServlet() {

    @Reference
    @Transient
    private lateinit var scriptStorage: ScriptStorage

    @Reference
    @Transient
    private lateinit var scriptFinder: ScriptFinder

    override fun doPost(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val all = request.getParameter("confirmation")
        val fileName = request.getParameter("file")
        val resourceResolver = request.resourceResolver

        if (fileName != null) {
            removeSingleFile(resourceResolver, response, fileName)
        } else if (all != null) {
            removeAllFiles(resourceResolver, response, all)
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            ServletUtils.writeMessage(response, "error", "Invalid arguments specified")
        }
    }

    private fun removeSingleFile(resourceResolver: ResourceResolver, response: SlingHttpServletResponse, fileName: String) {
        if (StringUtils.isEmpty(fileName)) {
            ServletUtils.writeMessage(response, "error", "File name to be removed cannot be empty")
        } else {
            val script = scriptFinder.find(fileName, resourceResolver)
            if (script == null) {
                ServletUtils.writeMessage(response, "error", "Script not found: $fileName")
            } else {
                val scriptPath = script.path

                try {
                    scriptStorage.remove(script, resourceResolver)
                    ServletUtils.writeMessage(response, "success", "Script removed successfully: $scriptPath")
                } catch (e: RepositoryException) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    ServletUtils.writeMessage(response, "error", "Cannot remove script: $scriptPath. Repository error: ${e.message}")
                }
            }
        }
    }

    private fun removeAllFiles(resourceResolver: ResourceResolver, response: SlingHttpServletResponse, all: String) {
        //todo Kotlin way?
        if (!java.lang.Boolean.parseBoolean(all)) {
            ServletUtils.writeMessage(response, "error", "Remove all scripts is not confirmed")
        } else {
            var paths: MutableList<String> = mutableListOf()
            val scripts = scriptFinder.findAll(resourceResolver)

            try {
                for (script in scripts) {
                    val path = script.path
                    scriptStorage.remove(script, resourceResolver)
                    paths.add(path)
                }
                val context: MutableMap<String, Any> = hashMapOf()
                context.put("paths", paths)
                ServletUtils.writeMessage(response, "success", "Scripts removed successfully, total: ${scripts.size}", context)
            } catch (e: RepositoryException) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                ServletUtils.writeMessage(response, "error", "Cannot save scripts removal. Repository error: ${e.message}")
            }
        }
    }
}