/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.ui.datasources;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.cognifide.apm.core.Property;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

@Component(
    immediate = true,
    service = Servlet.class,
    property = {
        Property.RESOURCE_TYPE + "apm/datasource/children",
        Property.DESCRIPTION + "Provides children of datasource",
        Property.VENDOR
    }
)
public class ChildrenDatasourceServlet extends SlingSafeMethodsServlet {

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    Iterator<Resource> iterator = Optional.ofNullable(request.getResource())
        .flatMap(parent -> Optional.ofNullable(parent.getChild("items")))
        .map(items -> items.listChildren())
        .orElse(Collections.emptyIterator());
    DataSource dataSource = new SimpleDataSource(iterator);
    request.setAttribute(DataSource.class.getName(), dataSource);
  }

}
