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

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.logger.ProgressEntry;
import com.cognifide.cq.cqsm.api.progress.ProgressHelper;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.core.jobs.JobResultsCache;
import com.cognifide.cq.cqsm.core.progress.ProgressImpl;
import java.util.List;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = SlingHttpServletRequest.class)
public final class ImportSummaryModel {

	public static final String MODE_PARAMETER = "mode";

	public static final String FILE_PARAMETER = "fileName";

	public static final String JOB_ID_PARAMETER = "progressJobId";

	private static final String EXECUTION = "execution-result";

	private final Progress progressLogger;

	private final Mode mode;

	private final Script script;

	@Inject
	public ImportSummaryModel(SlingHttpServletRequest request, @OSGiService ScriptManager scriptManager,
			@OSGiService JobResultsCache jobResultsCache, @OSGiService ScriptFinder scriptFinder)
			throws RepositoryException, PersistenceException {
		ResourceResolver resolver = request.getResourceResolver();
		this.mode = request.getParameter(MODE_PARAMETER) != null ?
				Mode.valueOf(request.getParameter(MODE_PARAMETER)) :
				null;
		final String scriptPath = request.getParameter(FILE_PARAMETER);
		final String progressJobId = request.getParameter(JOB_ID_PARAMETER);

		this.script = scriptFinder.find(scriptPath, resolver);
		Progress progress;
		if (StringUtils.isNotBlank(progressJobId)) {
			progress = jobResultsCache.get(progressJobId);
			if (progress == null) {
				progress = new ProgressImpl(resolver.getUserID());
			}
		} else {
			progress = scriptManager.process(script, mode, resolver);
		}
		this.progressLogger = progress;
	}

	public List<ProgressEntry> getProgressEntries() {
		return progressLogger.getEntries();
	}

	public String getMessageType() {
		if (!progressLogger.isSuccess()) {
			return "error";
		}
		if (mode.equals(Mode.DRY_RUN)) {
			return "info";
		}
		return "success";
	}

	public String getJson() {
		return ProgressHelper.toJson(progressLogger.getEntries());
	}

	public String getFileName() {
		if (script == null) {
			return EXECUTION + ".txt";
		} else {
			return EXECUTION + "-" + StringUtils.replace(script.getPath(), ".cqsm", ".txt");
		}
	}
}
