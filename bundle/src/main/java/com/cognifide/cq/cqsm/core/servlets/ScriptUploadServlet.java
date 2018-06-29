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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.scripts.ScriptUtils;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@SlingServlet(paths = {"/bin/cqsm/fileUpload"}, methods = {"POST"})
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM File Upload Servlet"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
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
				Map<String, Object> processingInfo = new HashMap<>();

				processingInfo.put("uploadedScripts",
						ScriptUtils.convertToMaps(scripts));
				ServletUtils.writeMessage(response, "success",
						"File successfully saved", processingInfo);
			}
		} catch (RepositoryException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ServletUtils.writeMessage(response, "error", "Cannot save script in repository: " + e.getMessage());
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
