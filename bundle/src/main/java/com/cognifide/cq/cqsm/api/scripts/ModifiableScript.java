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

import java.util.Date;
import org.apache.sling.api.resource.PersistenceException;

public interface ModifiableScript {

	/**
	 * Set validation status
	 */
	void setValid(Boolean flag) throws PersistenceException;

	/**
	 * Mark after execution
	 */
	void setExecuted(Boolean flag) throws PersistenceException;

	/**
	 * Enable or disable automatic execution
	 */
	void setExecutionEnabled(Boolean flag) throws PersistenceException;

	/**
	 * Set mode related with automatic execution
	 */
	void setExecutionMode(ExecutionMode mode) throws PersistenceException;

	/**
	 * Set date after which script will be executed by schedule executor
	 */
	void setExecutionSchedule(Date date) throws PersistenceException;

	/**
	 * Set dry run result
	 */
	void setDryRunStatus(Boolean flag) throws PersistenceException;

	/**
	 * Set publish run
	 */
	void setPublishRun(Boolean flag) throws PersistenceException;

	/**
	 * Set dry run execution date
	 */
	void setDryRunExecution(Date executionDate) throws PersistenceException;

}
