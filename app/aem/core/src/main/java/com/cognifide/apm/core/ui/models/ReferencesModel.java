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

package com.cognifide.apm.core.ui.models;

import com.cognifide.apm.api.services.DefinitionsProvider;
import com.cognifide.apm.core.actions.ActionFactory;
import com.cognifide.apm.core.actions.CommandDescription;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = SlingHttpServletRequest.class)
public class ReferencesModel {

  private final List<CommandDescription> references;

  private final Map<String, String> definitions;

  @Inject
  public ReferencesModel(@OSGiService ActionFactory actionFactory, @OSGiService DefinitionsProvider definitionsProvider) {
    this.references = actionFactory.getCommandDescriptions();
    this.definitions = definitionsProvider.getPredefinedDefinitions();
  }

  public List<CommandDescription> getReferences() {
    return references;
  }

  public Map<String, String> getDefinitions() {
    return definitions;
  }
}
