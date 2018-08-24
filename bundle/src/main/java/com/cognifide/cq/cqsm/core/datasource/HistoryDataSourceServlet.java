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
package com.cognifide.cq.cqsm.core.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.cognifide.cq.cqsm.api.history.History;
import com.cognifide.cq.cqsm.core.Property;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		service = Servlet.class,
		property = {
				Property.RESOURCE_TYPE + "apm/datasource/history",
				Property.DESCRIPTION + "Provides data source for history page",
				Property.VENDOR
		}
)
public class HistoryDataSourceServlet extends SlingSafeMethodsServlet {

	@Reference
	private History history;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		DataSource dataSource = new SimpleDataSource(history.findAllResource(request.getResourceResolver()).iterator());
		request.setAttribute(DataSource.class.getName(), dataSource);
	}
}
