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

import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = Servlet.class,
    property = {
        Property.RESOURCE_TYPE + "cqsm/core/renderers/referenceRenderer",
        Property.SELECTOR + "action",
        Property.EXTENSION + "json",
        Property.DESCRIPTION + "CQSM Action Reference Servlet",
        Property.VENDOR
    }
)
public class ReferenceServlet extends SlingAllMethodsServlet {

  @Reference
  private ActionFactory actionFactory;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    ServletUtils.writeJson(response, actionFactory.refer());
  }
}
