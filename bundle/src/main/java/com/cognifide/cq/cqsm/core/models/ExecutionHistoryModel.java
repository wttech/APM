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

import com.cognifide.cq.cqsm.api.history.Entry;
import com.cognifide.cq.cqsm.core.history.History;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public final class ExecutionHistoryModel {

	private List<Entry> executions;

	private final String filter;

	@Inject
	public ExecutionHistoryModel(@OSGiService History history, SlingHttpServletRequest request) {
		this.filter = request.getParameter("filter");
		executions = new ArrayList<>();
		for (Entry entry : history.findAll()) {
			executions.add(entry);
		}
		Collections.sort(executions);
	}

	public List<Entry> getExecutions() {
		if (StringUtils.isNotBlank(filter)) {
			CollectionUtils.filter(executions, new ExecutionHistoryFilter(filter));
		}
		return executions;
	}

	private class ExecutionHistoryFilter implements Predicate {

		private static final String FILTER_AUTOMATIC_RUN = "automatic run";

		private static final String FILTER_AUTHOR = "author";

		private static final String FILTER_PUBLISH = "publish";

		private final String filterType;

		private ExecutionHistoryFilter(String filterType) {
			this.filterType = filterType;
		}

		@Override
		public boolean evaluate(Object object) {
			Entry executionModel = (Entry) object;
			String value;
			switch (filterType) {
				case FILTER_AUTHOR:
				case FILTER_PUBLISH: {
					value = executionModel.getInstanceType();
				}
				break;
				case FILTER_AUTOMATIC_RUN: {
					value = executionModel.getExecutor();
				}
				break;
				default: {
					value = null;
				}
			}
			return filterType.equals(value);
		}
	}
}
