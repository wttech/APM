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
package com.cognifide.cq.cqsm.core.scripts;

import com.day.cq.commons.jcr.JcrConstants;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
@Getter
public class ScriptContent {

	public static final String CQSM_DRY_RUN_SUCCESSFUL = "cqsm:dryRunSuccessful";

	public static final String CQSM_EXECUTION_MODE = "cqsm:executionMode";

	public static final String CQSM_EXECUTION_SCHEDULE = "cqsm:executionSchedule";

	public static final String CQSM_EXECUTION_LAST = "cqsm:executionLast";

	public static final String CQSM_EXECUTION_SUMMARY = "cqsm:executionSummary";

	public static final String CQSM_EXECUTION_ENABLED = "cqsm:executionEnabled";

	public static final String CQSM_FILE = "cqsm:File";

	public static final String CQSM_PUBLISH_RUN = "cqsm:publishRun";

	public static final String CQSM_REPLICATED_BY = "cqsm:replicatedBy";

	public static final String CQSM_VERIFIED = "cqsm:verified";

	public static final String CQSM_DRY_RUN_LAST = "cqsm:dryRunLast";

	@Inject
	@Named(CQSM_VERIFIED)
	@Optional
	private Boolean verified;

	@Inject
	@Named(CQSM_DRY_RUN_SUCCESSFUL)
	@Optional
	private Boolean dryRunSuccessful;

	@Inject
	@Named(CQSM_PUBLISH_RUN)
	@Optional
	private Boolean publishRun;

	@Inject
	@Named(CQSM_EXECUTION_MODE)
	@Optional
	private String executionMode;

	@Inject
	@Named(CQSM_EXECUTION_SCHEDULE)
	@Optional
	private Date executionSchedule;

	@Inject
	@Named(CQSM_EXECUTION_SUMMARY)
	@Optional
	private String executionSummary;

	@Inject
	@Named(CQSM_EXECUTION_LAST)
	@Optional
	private Date executionLast;

	@Inject
	@Named(CQSM_EXECUTION_ENABLED)
	@Optional
	private Boolean executionEnabled;

	@Inject
	@Named(JcrConstants.JCR_LASTMODIFIED)
	@Optional
	private Date lastModified;

	@Inject
	@Named(CQSM_DRY_RUN_LAST)
	@Optional
	private Date dryRunLast;

	@Inject
	@Named(JcrConstants.JCR_DATA)
	@Optional
	private String data; //FIXME lazy load would be better here

	@Inject
	@Named(CQSM_REPLICATED_BY)
	@Optional
	private String replicatedBy;

}
