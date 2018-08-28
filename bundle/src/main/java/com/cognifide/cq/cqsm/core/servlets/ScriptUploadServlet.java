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

import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.scripts.ScriptUtils;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;
import com.google.common.collect.Maps;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@Component(
		immediate = true,
		service = Servlet.class,
		property = {
				Property.PATH + "/bin/cqsm/fileUpload",
				Property.METHOD + "POST",
				Property.DESCRIPTION + "CQSM File Upload Servlet",
				Property.VENDOR
		}
)
public class ScriptUploadServlet extends SlingAllMethodsServlet {

	private static final String REDIRECT_URL = "/etc/cqsm.html";

	@Reference
	private ScriptStorage scriptStorage;

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			final Map<String, InputStream> files = Maps.newHashMap();
			for (RequestParameter file : request.getRequestParameters("file")) {
				files.put(file.getFileName(), file.getInputStream());
			}

			final List<Script> scripts = scriptStorage
					.saveAll(files, isOverwrite(request), request.getResourceResolver());

			if (shouldRedirect(request)) {
				response.sendRedirect(getRedirectTo(request));
			} else {
				ServletUtils.writeJson(response, ScriptUtils.toJson(scripts));
			}
		} catch (RepositoryException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ServletUtils.writeJson(response, "Cannot save script in repository: " + e.getMessage());
		}
	}

	private boolean shouldRedirect(SlingHttpServletRequest request) {
		return BooleanUtils.toBoolean(request.getParameter("redirect"));
	}

	private boolean isOverwrite(SlingHttpServletRequest request) {
		return BooleanUtils.toBoolean(request.getParameter("overwrite"));
	}

	private String getRedirectTo(SlingHttpServletRequest request) {
		return StringUtils.defaultIfEmpty(request.getParameter("redirectTo"), REDIRECT_URL);
	}
}
