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

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@Component(
		immediate = true,
		service = Servlet.class,
		property = {
				Property.PATH + "/bin/cqsm/validate",
				Property.DESCRIPTION + "CQSM Validation Servlet",
				Property.VENDOR
		}
)
public class ScriptValidationServlet extends SlingAllMethodsServlet {

	@Reference
	private ScriptManager scriptManager;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final String content = request.getParameter("content");
		if (StringUtils.isEmpty(content)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ServletUtils.writeMessage(response, "error", "Script content is required");
			return;
		}

		try {
			final Progress progress = scriptManager.evaluate(content, Mode.VALIDATION, request.getResourceResolver());
			if (progress.isSuccess()) {
				ServletUtils.writeMessage(response, "success", "Script passes validation");
			} else {
				final String message = progress.getLastError().getLastMessageText();
				final Map<String, Object> context = new HashMap<>();

				if (message != null) {
					context.put("error", message);
				}

				ServletUtils.writeMessage(response, "error", "Script does not pass validation", context);
			}
		} catch (RepositoryException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ServletUtils.writeMessage(response, "error", String.format(
					"Script' cannot be validated because of " + "repository error: %s", e.getMessage()));
		}
	}
}