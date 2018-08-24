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
package com.cognifide.cq.cqsm.api.history;


import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.ProgressEntry;
import com.cognifide.cq.cqsm.api.progress.ProgressHelper;
import com.google.common.collect.ComparisonChain;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@EqualsAndHashCode
public class Entry implements Comparable<Entry> {

	public static final String SCRIPT_HISTORY_FILE_NAME = "script";

	private static final String EXECUTION = "execution-result";

	private static final String HISTORY_PAGE_DATE_FORMAT = "MMM dd, yyyy hh:mm:ss a";

	@Inject
	@Getter
	private String fileName;

	@Inject
	@Getter
	private Date executionTime;

	@Getter
	private String executionTimeFormatted;

	@Inject
	@Getter
	private String author;

	@Inject
	@Getter
	private Date uploadTime;

	@Inject
	@Getter
	private String instanceType;

	@Inject
	@Getter
	private String instanceHostname;

	@Inject
	@Getter
	@Named(ModifiableEntryBuilder.PROGRESS_LOG_PROPERTY)
	private transient String executionSummaryJson;

	@Inject
	private String mode;

	@Getter
	@Inject
	private String executor;

	@Getter
	private List<ProgressEntry> executionSummary;

	@Getter
	private final String path;

	@Getter
	private String filePath;

	@Getter
	private Date lastModified;

	@Getter
	private String lastModifiedTimeFormatted;

	@Getter
	private Boolean isRunSuccessful;

	@Getter
	private Boolean isDryRunSuccessful;

	@Getter
	private Date lastDryExecution;

	@Getter
	private String lastDryExecutionTimeFormatted;

	public Entry(Resource resource) {
		this.path = resource.getPath();
		processHistoryData(resource);
	}

	@Override
	public int compareTo(Entry other) {
		return ComparisonChain.start().compare(other.executionTime, this.executionTime).result();
	}

	public String getExecutionResultFileName() {
		return EXECUTION + "-" + StringUtils.replace(fileName, ".cqsm", ".txt");
	}

	private String getExecutorValue() {
		Mode modeType = Mode.fromString(mode, Mode.DRY_RUN);
		return StringUtils.isNotBlank(executor) ? executor : modeType.getName();
	}

	private void processHistoryData(Resource resource) {
		boolean isHistoryResource = resource.getChild(SCRIPT_HISTORY_FILE_NAME) != null;
		if (isHistoryResource) {
			final HistoryResourceAdapter historyResourceAdapter = new HistoryResourceAdapter(resource);

			this.lastModified = historyResourceAdapter.getLastModification();
			this.lastDryExecution = historyResourceAdapter.getLastDryRun();
			this.isDryRunSuccessful = historyResourceAdapter.isLastDryRunSuccessful();
			this.filePath = historyResourceAdapter.getFilePath();
		}
	}

	@PostConstruct
	protected void init() {
		lastModifiedTimeFormatted = transferDateToHistoryFormat(lastModified);
		lastDryExecutionTimeFormatted = transferDateToHistoryFormat(lastDryExecution);
		executionTimeFormatted = transferDateToHistoryFormat(executionTime);

		executor = getExecutorValue();

		executionSummary = ProgressHelper.fromJson(executionSummaryJson);
		this.isRunSuccessful = ProgressHelper.hasNoErrors(executionSummary);
	}

	private String transferDateToHistoryFormat(Date date) {
		return Optional.ofNullable(date)
				.map(dateVal -> DateFormatUtils.format(dateVal, HISTORY_PAGE_DATE_FORMAT, Locale.getDefault()))
				.orElse(StringUtils.EMPTY);
	}
}