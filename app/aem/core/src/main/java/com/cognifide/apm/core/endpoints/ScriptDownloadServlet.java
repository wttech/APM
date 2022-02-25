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

import com.cognifide.apm.core.Property;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
		immediate = true,
		service = Servlet.class,
		property = {
				Property.PATH + "/bin/cqsm/fileDownload",
				Property.METHOD + "GET",
				Property.DESCRIPTION + "CQSM File Download Servlet",
				Property.VENDOR
		}
)
public class ScriptDownloadServlet extends SlingSafeMethodsServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptDownloadServlet.class);

	private static final int BYTES_DOWNLOAD = 1024;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		String fileName = request.getParameter("filename");
		String filePath = request.getParameter("filepath");

		String mode = request.getParameter("mode");

		try {
			final ResourceResolver resourceResolver = request.getResourceResolver();
			final Session session = resourceResolver.adaptTo(Session.class);

			if (!("view").equals(mode)) {
				response.setContentType("application/octet-stream"); // Your content type
				response.setHeader("Content-Disposition",
						"attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
			}

			String path = StringUtils.replace(filePath, "_jcr_content", "jcr:content");

			Node jcrContent = session.getNode(path + "/jcr:content");

			InputStream input = jcrContent.getProperty("jcr:data").getBinary().getStream();

			session.save();
			int read;
			byte[] bytes = new byte[BYTES_DOWNLOAD];
			OutputStream os = response.getOutputStream();

			while ((read = input.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			input.close();
			os.flush();
			os.close();

		} catch (RepositoryException e) {
			LOGGER.error(e.getMessage(), e);
			response.sendRedirect("/etc/cqsm.html");
			// response.sendError(500);
		}
	}

}