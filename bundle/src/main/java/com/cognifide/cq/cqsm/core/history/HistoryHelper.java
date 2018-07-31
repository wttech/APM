/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2018 Cognifide Limited
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
package com.cognifide.cq.cqsm.core.history;

import com.cognifide.cq.cqsm.api.logger.ProgressEntry;
import com.cognifide.cq.cqsm.api.logger.Status;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;

public final class HistoryHelper {

	public static final String SCRIPT_HISTORY_FILE_NAME = "script";

	private HistoryHelper() {
	}


	public static Boolean isRunSuccessful(List<ProgressEntry> progressSummary) {
		Boolean isSuccessful = Boolean.TRUE;
		final Iterator<ProgressEntry> executionSummaryIterator = progressSummary.iterator();

		while (executionSummaryIterator.hasNext() && isSuccessful) {
			final ProgressEntry operation = executionSummaryIterator.next();
			if (Status.ERROR.equals(operation.getStatus())) {
				isSuccessful = Boolean.FALSE;
			}
		}
		return isSuccessful;
	}

	public static Boolean isHistoryResource(Resource resource) {
		return resource.getChild(SCRIPT_HISTORY_FILE_NAME) != null;
	}

	public static String generateHistoryPageRunDateFormat(Date date, String instanceType) {
		return String.format("%s %s", StringUtils.capitalize(instanceType), createHistoryPageDateStr(date));
	}

	public static String generateHistoryPageDateFormat(Date date) {
		String fullDateStr = createHistoryPageDateStr(date);

		return fullDateStr;
	}

	private static String createHistoryPageDateStr(Date date) {
		Locale locale = Locale.getDefault();
		String dateStr = new SimpleDateFormat("dd, yyyy hh:mm:ss a", locale).format(date);
		String monthStr = StringUtils.capitalize(new SimpleDateFormat("MMM", locale).format(date));
		return String.format("%s %s", monthStr, dateStr);
	}
}
