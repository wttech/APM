/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
package com.cognifide.apm.core.endpoints;

import com.cognifide.apm.core.endpoints.response.ResponseEntity;
import com.cognifide.apm.core.endpoints.utils.RequestProcessor;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.ServletException;
import java.io.IOException;

public abstract class AbstractFormServlet<F> extends SlingAllMethodsServlet {

  @Reference
  protected transient ModelFactory modelFactory;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
    new RequestProcessor<>(modelFactory, getFormClass())
        .process(request, response, this::doPost);
  }

  protected abstract Class<F> getFormClass();

  protected abstract ResponseEntity doPost(F form, ResourceResolver resolver) throws Exception;
}
