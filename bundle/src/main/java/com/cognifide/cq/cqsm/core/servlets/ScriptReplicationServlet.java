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
package com.cognifide.cq.cqsm.core.servlets;

import com.cognifide.cq.cqsm.api.exceptions.ExecutionException;
import com.cognifide.cq.cqsm.api.scripts.ModifiableScript;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptReplicator;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.scripts.ModifiableScriptWrapper;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;
import com.day.cq.replication.ReplicationException;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@SlingServlet(paths = {"/bin/cqsm/replicate"}, methods = {"GET"})
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Replicate Servlet"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
public class ScriptReplicationServlet extends SlingSafeMethodsServlet {

	private static final String PUBLISH_RUN = "publish";

	@Reference
	private ScriptReplicator scriptReplicator;

	@Reference
	private ScriptFinder scriptFinder;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		ResourceResolver resolver = request.getResourceResolver();

		final String searchPath = request.getParameter("fileName");
		final String run = request.getParameter("run");

		if (StringUtils.isEmpty(searchPath)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ServletUtils.writeMessage(response, "error", "File name parameter is required");
			return;
		}

		final Script script = scriptFinder.find(searchPath, resolver);
		if (script == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			ServletUtils
					.writeMessage(response, "error", String.format("Script cannot be found: %s", searchPath));
			return;
		}

		final String scriptPath = script.getPath();

		try {
			final ModifiableScript modifiableScript = new ModifiableScriptWrapper(resolver, script);
			if (PUBLISH_RUN.equals(run)) {
				modifiableScript.setPublishRun(true);
			}
			scriptReplicator.replicate(script, resolver);

			ServletUtils.writeMessage(response, "success",
					String.format("Script '%s' replicated successfully", scriptPath));
		} catch (PersistenceException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ServletUtils.writeMessage(response, "error",
					String.format("Script '%s' cannot be processed because of repository error: %s",
																		 scriptPath, e.getMessage()));
		} catch (ExecutionException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ServletUtils.writeMessage(response, "error",
					String.format("Script '%s' cannot be processed: %s", scriptPath, e.getMessage()));
		} catch (ReplicationException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ServletUtils.writeMessage(response, "error",
					String.format("Script '%s' cannot be replicated: %s", scriptPath, e.getMessage()));
		}
	}
}