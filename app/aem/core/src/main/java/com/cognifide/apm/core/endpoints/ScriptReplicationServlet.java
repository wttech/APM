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
package com.cognifide.apm.core.endpoints;

import com.cognifide.apm.api.exceptions.ExecutionException;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.scripts.MutableScriptWrapper;
import com.cognifide.apm.core.scripts.ScriptReplicator;
import com.cognifide.apm.core.utils.ServletUtils;
import com.day.cq.replication.ReplicationException;
import java.io.IOException;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/cqsm/replicate",
        Property.METHOD + HttpConstants.METHOD_GET,
        Property.DESCRIPTION + "CQSM Replicate Servlet",
        Property.VENDOR
    }
)
public class ScriptReplicationServlet extends SlingSafeMethodsServlet {

  private static final String PUBLISH_RUN = "publish";

  @Reference
  private transient ScriptReplicator scriptReplicator;

  @Reference
  private transient ScriptFinder scriptFinder;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    ResourceResolver resolver = request.getResourceResolver();

    final String searchPath = request.getParameter("fileName");
    final String run = request.getParameter("run");

    if (StringUtils.isEmpty(searchPath)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ServletUtils.writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE, "File name parameter is required");
      return;
    }

    final Script script = scriptFinder.find(searchPath, resolver);
    if (script == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ServletUtils
          .writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE, String.format("Script cannot be found: %s", searchPath));
      return;
    }

    final String scriptPath = script.getPath();

    try {
      if (PUBLISH_RUN.equals(run)) {
        MutableScriptWrapper modifiableScript = new MutableScriptWrapper(script);
        modifiableScript.setPublishRun(true);
        modifiableScript.setReplicatedBy(resolver.getUserID());
      }
      scriptReplicator.replicate(script, resolver);

      ServletUtils.writeMessage(response, ServletUtils.SUCCESS_RESPONSE_TYPE,
          String.format("Script '%s' replicated successfully", scriptPath));
    } catch (PersistenceException | RepositoryException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      ServletUtils.writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE,
          String.format("Script '%s' cannot be processed because of repository error: %s",
              scriptPath, e.getMessage()));
    } catch (ExecutionException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      ServletUtils.writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE,
          String.format("Script '%s' cannot be processed: %s", scriptPath, e.getMessage()));
    } catch (ReplicationException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      ServletUtils.writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE,
          String.format("Script '%s' cannot be replicated: %s", scriptPath, e.getMessage()));
    }
  }
}