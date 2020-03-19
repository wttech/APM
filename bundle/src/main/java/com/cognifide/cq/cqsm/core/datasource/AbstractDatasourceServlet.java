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
package com.cognifide.cq.cqsm.core.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.WordUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

public abstract class AbstractDatasourceServlet<E> extends SlingSafeMethodsServlet {

  private final List<E> options;

  protected AbstractDatasourceServlet(List<E> options) {
    this.options = options;
  }

  protected AbstractDatasourceServlet(E[] options) {
    this.options = Arrays.asList(options);
  }

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    SimpleDataSourceBuilder builder = new SimpleDataSourceBuilder(request.getResourceResolver());
    options.forEach(item -> builder.addOption(getName(item.toString()), item.toString()));
    request.setAttribute(DataSource.class.getName(), builder.build());
  }

  private String getName(String item) {
    String words = item.replace('_', ' ');
    words = WordUtils.capitalizeFully(words.toLowerCase());
    return words;
  }
}
