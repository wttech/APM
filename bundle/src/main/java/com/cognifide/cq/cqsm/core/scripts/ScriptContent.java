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


	public static final String CQSM_EXECUTION_MODE = "cqsm:executionMode";

	public static final String CQSM_EXECUTION_SCHEDULE = "cqsm:executionSchedule";

	public static final String CQSM_EXECUTION_LAST = "cqsm:executionLast";

	public static final String CQSM_EXECUTION_ENABLED = "cqsm:executionEnabled";

	public static final String CQSM_FILE = "cqsm:File";

	public static final String CQSM_PUBLISH_RUN = "cqsm:publishRun";

  public static final String CQSM_REPLICATED_BY = "cqsm:replicatedBy";

	public static final String CQSM_VERIFIED = "cqsm:verified";

	public static final String DRY_RUN_TIME = "cqsm:dryRunTime";

	public static final String DRY_RUN_SUMMARY = "cqsm:dryRunSummary";

	public static final String DRY_RUN_SUCCESSFUL = "cqsm:dryRunSuccessful";

	public static final String RUN_TIME = "cqsm:runTime";

	public static final String RUN_SUMMARY = "cqsm:runSummary";

	public static final String RUN_SUCCESSFUL = "cqsm:runSuccessful";

	public static final String RUN_ON_PUBLISH_TIME = "cqsm:runOnPublishTime";

	public static final String RUN_ON_PUBLISH_SUMMARY = "cqsm:runOnPublishSummary";

	public static final String RUN_ON_PUBLISH_SUCCESSFUL = "cqsm:runOnPublishSuccessful";

	@Inject
	@Named(CQSM_VERIFIED)
	@Optional
	private Boolean verified;

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
	@Named(JcrConstants.JCR_DATA)
	@Optional
	private String data; //FIXME lazy load would be better here

  @Inject
  @Named(CQSM_REPLICATED_BY)
  @Optional
  private String replicatedBy;

	@Inject
	@Named(DRY_RUN_TIME)
	@Optional
	private Date dryRunTime;

	@Inject
	@Named(DRY_RUN_SUMMARY)
	@Optional
	private String dryRunSummary;

	@Inject
	@Named(DRY_RUN_SUCCESSFUL)
	@Optional
	private Boolean dryRunSuccessful;

	@Inject
	@Named(RUN_TIME)
	@Optional
	private Date runTime;

	@Inject
	@Named(RUN_SUMMARY)
	@Optional
	private String runSummary;

	@Inject
	@Named(RUN_SUCCESSFUL)
	@Optional
	private Boolean runSuccessful;

	@Inject
	@Named(RUN_ON_PUBLISH_TIME)
	@Optional
	private Date runOnPublishTime;

	@Inject
	@Named(RUN_ON_PUBLISH_SUMMARY)
	@Optional
	private String runOnPublishSummary;

	@Inject
	@Named(RUN_ON_PUBLISH_SUCCESSFUL)
	@Optional
	private Boolean runOnPublishSuccessful;
}
