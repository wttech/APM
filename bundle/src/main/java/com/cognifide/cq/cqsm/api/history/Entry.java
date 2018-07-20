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
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.core.history.HistoryHelper;
import com.cognifide.cq.cqsm.core.progress.ProgressHelper;
import com.cognifide.cq.cqsm.core.scripts.ScriptImpl;
import com.google.common.collect.ComparisonChain;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Entry implements Comparable<Entry> {

	private static final Logger LOG = LoggerFactory.getLogger(Entry.class);

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
	private final String filePath;

	@Getter
	private Timestamp lastModified;

	@Getter
	private Timestamp lastExecution;

	@Getter
	private Timestamp lastDryExecution;

	@Getter
	private Boolean isSuccessful;


	// TODO: Perform refactoring
	public Entry(Resource resource) {
		final Resource scriptResource = resource.getChild("script");
		if (scriptResource != null) {
			Script script = scriptResource.adaptTo(ScriptImpl.class);
			this.lastModified = new Timestamp(script.getLastModified().getTime());
			this.lastModified.setTime(script.getLastModified().getTime());

			final Date lastExecutionDate = script.getExecutionLast();

			if (lastExecutionDate != null) {
				this.lastExecution = new Timestamp(lastExecutionDate.getTime());
			}

			final Date lastDryExecutionDate = script.getDryRunLast();
			if (lastDryExecutionDate != null) {
				this.lastDryExecution = new Timestamp(lastDryExecutionDate.getTime());
			}


		} else {
			LOG.error("HISTORY_UTIL_NO_SCRIPT",
				String.format("Can't find script for resource: {}", resource.getPath()));
		}

		this.path = resource.getPath();
		this.filePath = getFilePath(resource);
	}

	@PostConstruct
	protected void init() {
		executor = getExecutorValue();
		//FIXME api->core relationship
		executionSummary = ProgressHelper.fromJson(executionSummaryJson);
		this.isSuccessful = HistoryHelper.isSuccessful(executionSummary);
	}

	public String getExecutionResultFileName() {
		return EXECUTION + "-" + StringUtils.replace(fileName, ".cqsm", ".txt");
	}

	private String getFilePath(Resource resource) {
		String path = null;
		if (resource.getChild("script") != null) {
			path = resource.getPath() + "/script";
		}
		return path;
	}

	private String getExecutorValue() {
		Mode modeType = Mode.fromString(mode, Mode.DRY_RUN);
		return StringUtils.isNotBlank(executor) ? executor : modeType.getName();
	}

	public String getLastExecutionStatus() {
		String lastExecutionDateStr = new SimpleDateFormat("dd, yyyy hh:mm:ss a").format(lastExecution);
		String lastExecutionMonthStr = StringUtils.capitalize(new SimpleDateFormat("MMM").format(lastExecution));
		String lastExecutionFullDateStr = String.format("%s %s", lastExecutionMonthStr, lastExecutionDateStr);

		return String.format("%s %s", StringUtils.capitalize(instanceType), lastExecutionFullDateStr);
	}

	public String getLastDryExecutionStatus() {
		String lastDryExecutionDateStr = new SimpleDateFormat("dd, yyyy hh:mm:ss a").format(lastDryExecution);
		String lastDryExecutionMonthStr = StringUtils.capitalize(new SimpleDateFormat("MMM").format(lastDryExecution));
		String lastDryExecutionFullDateStr = String.format("%s %s", lastDryExecutionMonthStr, lastDryExecutionDateStr);

		return String.format("%s %s", StringUtils.capitalize(instanceType), lastDryExecutionFullDateStr);
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
}
