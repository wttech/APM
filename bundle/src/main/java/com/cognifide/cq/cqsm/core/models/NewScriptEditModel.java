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
package com.cognifide.cq.cqsm.core.models;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;

import lombok.Getter;

@Model(adaptables = SlingHttpServletRequest.class)
public final class NewScriptEditModel {

	public static final String PATH_PARAM = "path";

	private static final Logger LOG = LoggerFactory.getLogger(NewScriptEditModel.class);

	private static final String FILE_NAME_DEFAULT = "filename";

	private static final String CONTENT_FILE = "content.cqsm";

	private static final String CONTENT_FILE_CHARSET = "UTF-8";

	@Getter
	private final String fileName;

	@Getter
	private final boolean edit;

	@Getter
	private final String content;

	@Inject
	public NewScriptEditModel(SlingHttpServletRequest request, @OSGiService ScriptFinder scriptFinder) {
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
			return IOUtils.toString(getClass().getResourceAsStream(CONTENT_FILE), CONTENT_FILE_CHARSET);
		} catch (IOException e) {
			LOG.warn("Cannot read content of default script template.", e);
			return "";
		}
	}
}
