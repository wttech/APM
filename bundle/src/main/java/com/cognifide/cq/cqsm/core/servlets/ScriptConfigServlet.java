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

import com.cognifide.cq.cqsm.api.scripts.ExecutionMode;
import com.cognifide.cq.cqsm.api.scripts.ModifiableScript;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.scripts.ModifiableScriptWrapper;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@SlingServlet(paths = {"/bin/cqsm/scriptConfig"}, methods = {"POST"})
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Execution Mode Servlet"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
public class ScriptConfigServlet extends SlingAllMethodsServlet {

	@Reference
	private ScriptFinder scriptFinder;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final ResourceResolver resolver = request.getResourceResolver();
		final String searchPath = request.getParameter("scriptPath");
		final Script script = scriptFinder.find(searchPath, resolver);
		if (script == null) {
			ServletUtils.writeMessage(response, "error", "Script not found: " + searchPath);
			return;
		}
		final ModifiableScript modifiableScript = new ModifiableScriptWrapper(resolver, script);
		try {
			final String executionMode = request.getParameter("executionMode");
			if (executionMode != null) {
				modifiableScript.setExecutionMode(ExecutionMode.valueOf(executionMode.toUpperCase()));
			}

			final String executionEnabled = request.getParameter("executionEnabled");
			if (executionEnabled != null) {
				modifiableScript.setExecutionEnabled(BooleanUtils.toBoolean(executionEnabled));
			}

			ServletUtils.writeMessage(response, "success", "Script configuration updated");
		} catch (PersistenceException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ServletUtils
					.writeMessage(response, "error", "Cannot update script configuration: " + e.getMessage());
		}
	}
}
