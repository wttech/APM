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

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class)
public final class ConsoleModel {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleModel.class);

  private static final String FILE_NAME_DEFAULT = "filename";

  private static final String CONTENT_FILE = "content.apm";

  private static final String CONTENT_FILE_CHARSET = "UTF-8";

  private final String fileName;

  private final boolean edit;

  private final String content;

  @Inject
  public ConsoleModel(SlingHttpServletRequest request, @OSGiService ScriptFinder scriptFinder) {
    String scriptPath = request.getRequestPathInfo().getSuffix();
    Script script = scriptFinder.find(scriptPath, request.getResourceResolver());
    edit = script != null;
    if (edit) {
      fileName = FilenameUtils.getBaseName(script.getPath());
      content = script.getData();
    } else {
      fileName = FILE_NAME_DEFAULT;
      content = getContentDefault();
    }
  }

  private String getContentDefault() {
    try {
      InputStream inputStream = getClass().getResourceAsStream(CONTENT_FILE);
      if (inputStream != null) {
        return IOUtils.toString(inputStream, CONTENT_FILE_CHARSET);
      }
    } catch (IOException e) {
      LOGGER.warn("Cannot read content of default script template.", e);
    }
    return "";
  }

  public String getFileName() {
    return fileName;
  }

  public boolean isEdit() {
    return edit;
  }

  public String getContent() {
    return content;
  }
}
