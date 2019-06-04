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
package com.cognifide.cq.cqsm.api.scripts;

import com.cognifide.cq.cqsm.core.scripts.Checksum;
import java.util.Date;
import org.apache.sling.api.resource.ResourceResolver;

public interface Script {

	/**
	 * Get validation status
	 */
	boolean isValid();

	/**
	 * Check whether is ready for automatic execution
	 */
	boolean isExecutionEnabled();

	/**
	 * Get mode related with automatic execution
	 */
	ExecutionMode getExecutionMode();

	/**
	 * Get date after which script will be executed by schedule executor
	 */
	Date getExecutionSchedule();

	/**
	 * Get last execution date
	 */
	Date getExecutionLast();

	/**
	 * Check whether content is modified in the meantime
	 */
	boolean isContentModified(ResourceResolver resolver);

	/**
	 * Check whether dry run was executed at least once
	 */
	boolean isDryRunExecuted();

	/**
	 * Check run on publish
	 */
	boolean isPublishRun();

	/**
	 * Returns the path for the script file
	 */
	String getPath();

	/**
	 * Returns checksum calculated based on the script content
	 */
	Checksum getChecksum();

	/**
	 * Return author of the file
	 */
	String getAuthor();

	/**
	 * Return last modified date
	 */
	Date getLastModified();

	/**
	 * Return copy of the file
	 */
	String getData();

	/**
	 * Return user that replicated script
	 * @return
	 */
	String getReplicatedBy();

	/**
	 * Return last dry run date
	 */
	Date getDryRunTime();

	/**
	 * Get path to dry run summary
	 */
	String getDryRunSummary();

	/**
	 * Check dry run result
	 */
	boolean isDryRunSuccessful();

	/**
	 * Return date of last run on author.
	 */
	Date getRunTime();

	/**
	 * Get path to summary of last run on author.
	 */
	String getRunSummary();

	/**
	 * Return status of last run on author.
	 */
	boolean isRunSuccessful();

	/**
	 * Get path to summary of last run on publish
	 */
	String getRunOnPublishSummary();

	/**
	 * Return date of last run on publish.
	 */
	Date getRunOnPublishTime();

	/**
	 * Return status of last run on publish.
	 */
	boolean isRunOnPublishSuccessful();

}
