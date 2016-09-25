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

import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage;
import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

@Model(adaptables = SlingHttpServletRequest.class)
public final class ImportInitModel {

	@Getter
	private final boolean onAuthor;

	@Getter
	private final String currentPath;

	private final ScriptFinder scriptFinder;

	@Getter
	private List<FileModel> files;

	@Inject
	public ImportInitModel(SlingHttpServletRequest request, @OSGiService ScriptFinder scriptFinder,
			@OSGiService ScriptStorage scriptStorage, @OSGiService InstanceTypeProvider instanceType) {
		this.currentPath = scriptStorage.getSavePath();
		this.scriptFinder = scriptFinder;
		this.files = loadFiles(request.getResourceResolver());
		this.onAuthor = instanceType.isOnAuthor();
	}

	private List<FileModel> loadFiles(ResourceResolver resolver) {
		final List<FileModel> loaded = new ArrayList<>();
		for (Script script : scriptFinder.findAll(resolver)) {
			loaded.add(new FileModel(script));
		}

		Collections.sort(loaded);

		return loaded;
	}
}
