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

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import com.cognifide.cq.cqsm.api.scripts.Script;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Comparator;
import java.util.Date;

import lombok.Getter;

//FIXME looks like ScriptImpl duplicate
public final class FileModel implements Comparable<FileModel> {

	private static final Comparator<Date> DATE_DESCENDING_NULL_LAST_ORDERING = Ordering.natural().reverse()
			.nullsLast();

	private static final Comparator<String> STRING_ASCENDING_NULL_LAST_ORDERING = Ordering.natural()
			.nullsLast();

	@Getter
	private final String fileName;

	@Getter
	private final String author;

	@Getter
	private final boolean executionEnabled;

	@Getter
	private final Date executionSchedule;

	@Getter
	private final Date lastExecuted;

	@Getter
	private Date lastModified;

	@Getter
	private boolean valid;

	@Getter
	private String path;

	@Getter
	private boolean dryRunExecuted;

	@Getter
	private boolean dryRunSuccessful;

	private final String executionMode;

	public FileModel(Script script) {
		this.fileName = FilenameUtils.getName(script.getPath());
		this.author = script.getAuthor();
		this.path = script.getPath();
		this.valid = script.isValid();

		this.lastModified = script.getLastModified();
		this.lastExecuted = script.getExecutionLast();
		this.dryRunSuccessful = script.isDryRunSuccessful();
		this.dryRunExecuted = script.isDryRunExecuted();
		this.executionMode = script.getExecutionMode().name();
		this.executionEnabled = script.isExecutionEnabled();
		this.executionSchedule = script.getExecutionSchedule();
	}

	@Override
	public int compareTo(FileModel other) {
		return ComparisonChain.start()
				.compare(this.lastModified, other.lastModified, DATE_DESCENDING_NULL_LAST_ORDERING)
				.compare(this.fileName, other.fileName, STRING_ASCENDING_NULL_LAST_ORDERING).result();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public String getExecutionMode() {
		return this.executionMode.toLowerCase();
	}
}
