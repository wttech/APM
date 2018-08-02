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
import com.cognifide.cq.cqsm.core.history.HistoryEntryPropsFactory;
import com.cognifide.cq.cqsm.core.progress.ProgressHelper;
import com.google.common.collect.ComparisonChain;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Entry implements Comparable<Entry> {

	public static final String SCRIPT_HISTORY_FILE_NAME = "script";

	private static final String EXECUTION = "execution-result";

	@Inject
	@Getter
	private String fileName;

	@Inject
	@Getter
	private Date executionTime;

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
	private Timestamp lastModified;

	@Getter
	private Boolean isRunSuccessful;

	@Getter
	private Boolean isDryRunSuccessful;

	@Getter
	private Timestamp lastDryExecution;

	public Entry(Resource resource) {
		boolean isHistoryResource = resource.getChild(SCRIPT_HISTORY_FILE_NAME) != null;
		if (isHistoryResource) {
			final HistoryEntryPropsFactory historyEntryPropsFactory = new HistoryEntryPropsFactory(resource);

			this.lastModified = historyEntryPropsFactory.getLastModificationTimestamp();
			this.lastDryExecution = historyEntryPropsFactory.getLastDryRunTimestamp();
			this.isDryRunSuccessful = historyEntryPropsFactory.isLastDryRunSuccessful();
			this.filePath = historyEntryPropsFactory.getFilePath();
		}
		this.path = resource.getPath();
	}

	@Override
	public int compareTo(Entry other) {
		return ComparisonChain.start().compare(other.executionTime, this.executionTime).result();
	}

	//FIXME: could be fixed with lombok
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public String getExecutionResultFileName() {
		return EXECUTION + "-" + StringUtils.replace(fileName, ".cqsm", ".txt");
	}

	private String getExecutorValue() {
		Mode modeType = Mode.fromString(mode, Mode.DRY_RUN);
		return StringUtils.isNotBlank(executor) ? executor : modeType.getName();
	}

	@PostConstruct
	protected void init() {
		executor = getExecutorValue();
		//FIXME api->core relationship

		executionSummary = ProgressHelper.fromJson(executionSummaryJson);
		this.isRunSuccessful = ProgressHelper.calculateSuccess(executionSummary);
	}
}