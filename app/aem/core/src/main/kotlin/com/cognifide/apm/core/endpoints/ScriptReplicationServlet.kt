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

import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.osgi.service.component.annotations.Component
import javax.jcr.RepositoryException
import javax.servlet.Servlet
import com.cognifide.apm.core.Property
import org.apache.sling.api.servlets.SlingSafeMethodsServlet
import org.osgi.service.component.annotations.Reference
import com.cognifide.apm.core.scripts.ScriptReplicator
import com.cognifide.apm.api.services.ScriptFinder
import javax.servlet.http.HttpServletResponse
import com.cognifide.apm.core.utils.ServletUtils
import com.cognifide.apm.core.scripts.MutableScriptWrapper
import com.day.cq.replication.ReplicationException
import org.apache.sling.api.resource.PersistenceException
import java.util.concurrent.ExecutionException

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/replicate",
            Property.METHOD + "GET",
            Property.DESCRIPTION + "APM Replication Servlet",
            Property.VENDOR
        ])
class ScriptReplicationServlet : SlingSafeMethodsServlet() {

    @Reference
    @Transient
    private lateinit var scriptReplicator: ScriptReplicator

    @Reference
    @Transient
    private lateinit var scriptFinder: ScriptFinder

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val searchPath = request.getParameter("fileName")
        val run = request.getParameter("run")
        val resourceResolver = request.resourceResolver

        if (StringUtils.isEmpty(searchPath)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            ServletUtils.writeMessage(response, "error", "File name parameter is required.")
        }

        val script = scriptFinder.find(searchPath, resourceResolver)
        if (script == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND)
            ServletUtils.writeMessage(response, "error", "Script cannot be found: $searchPath")
        }

        val scriptPath = script.path
        try {
            if (PUBLISH_RUN.equals(run)) {
                var modifiableScript = MutableScriptWrapper(script)
                modifiableScript.setPublishRun(true)
                modifiableScript.setReplicatedBy(resourceResolver.userID)
            }
            scriptReplicator.replicate(script, resourceResolver)

            ServletUtils.writeMessage(response, "success", "Script $scriptPath replicated successfully.")
        } catch (e: PersistenceException) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            ServletUtils.writeMessage(response, "error",
                    "Script $scriptPath cannot be processed because of repository error: ${e.message}")

        } catch (e: RepositoryException) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            ServletUtils.writeMessage(response, "error",
                    "Script $scriptPath cannot be processed because of repository error: ${e.message}")

        } catch (e: ExecutionException) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            ServletUtils.writeMessage(response, "error",
                    "Script $scriptPath cannot be processed: ${e.message}")

        } catch (e: ReplicationException) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            ServletUtils.writeMessage(response, "error",
                    "Script $scriptPath cannot be replicated: ${e.message}")
        }
    }

    companion object {
        const val PUBLISH_RUN = "publish"
    }
}
