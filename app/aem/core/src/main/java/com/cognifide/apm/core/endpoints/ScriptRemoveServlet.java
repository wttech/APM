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

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.scripts.ScriptStorage;
import com.cognifide.apm.core.utils.ServletUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		service = Servlet.class,
		property = {
				Property.PATH + "/bin/cqsm/remove",
				Property.METHOD + HttpConstants.METHOD_POST,
				Property.DESCRIPTION + "CQSM Remove Scripts Servlet",
				Property.VENDOR
		}
)
public class ScriptRemoveServlet extends SlingAllMethodsServlet {

	@Reference
	private transient ScriptStorage scriptStorage;

	@Reference
	private transient ScriptFinder scriptFinder;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final String all = request.getParameter("confirmation");
		final String fileName = request.getParameter("file");
		ResourceResolver resolver = request.getResourceResolver();
		if (fileName != null) {
			removeSingleFile(resolver, response, fileName);
		} else if (all != null) {
			removeAllFiles(resolver, response, all);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ServletUtils.writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE, "Invalid arguments specified");
		}
	}

	private void removeAllFiles(ResourceResolver resolver, SlingHttpServletResponse response, String all)
			throws IOException {
		if (!Boolean.parseBoolean(all)) {
			ServletUtils.writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE, "Remove all scripts is not confirmed");
		} else {
			final List<String> paths = new LinkedList<>();
			final List<Script> scripts = scriptFinder.findAll(resolver);

			try {
				for (Script script : scripts) {
					final String path = script.getPath();

					scriptStorage.remove(script, resolver);
					paths.add(path);
				}

				final Map<String, Object> context = new HashMap<>();
				context.put("paths", paths);

				ServletUtils.writeMessage(response, ServletUtils.SUCCESS_RESPONSE_TYPE,
						String.format("Scripts removed successfully, total: %d", scripts.size()), context);
			} catch (RepositoryException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ServletUtils.writeJson(response,
						"Cannot save remove all scripts. Repository error: " + e.getMessage());
			}
		}
	}

	private void removeSingleFile(ResourceResolver resolver, SlingHttpServletResponse response,
			String fileName) throws IOException {
		if (StringUtils.isEmpty(fileName)) {
			ServletUtils.writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE, "File name to be removed cannot be empty");
		} else {
			final Script script = scriptFinder.find(fileName, resolver);
			if (script == null) {
				ServletUtils
						.writeMessage(response, ServletUtils.ERROR_RESPONSE_TYPE, String.format("Script not found: '%s'", fileName));
			} else {
				final String scriptPath = script.getPath();

				try {
					scriptStorage.remove(script, resolver);

					ServletUtils.writeMessage(response, ServletUtils.SUCCESS_RESPONSE_TYPE,
							String.format("Script removed successfully: %s", scriptPath));
				} catch (RepositoryException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					ServletUtils.writeJson(response,
							String.format("Cannot remove script: '%s'." + " Repository error: %s", scriptPath,
									e.getMessage()));
				}
			}
		}
	}
}
