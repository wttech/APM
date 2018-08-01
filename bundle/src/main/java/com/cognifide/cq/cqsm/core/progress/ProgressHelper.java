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
package com.cognifide.cq.cqsm.core.progress;

import com.cognifide.cq.cqsm.api.logger.Status;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.cognifide.cq.cqsm.api.logger.ProgressEntry;

import java.util.Arrays;
import java.util.List;

public final class ProgressHelper {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private ProgressHelper() {
	}

	public static List<ProgressEntry> fromJson(String executionSummaryJson) {
		return Arrays.asList(GSON.fromJson(executionSummaryJson, ProgressEntry[].class));
	}

	public static String toJson(List<ProgressEntry> entries) {
		return GSON.toJson(entries.toArray());
	}

	public static String toJson(ProgressEntry entry) {
		return GSON.toJson(entry);
	}

	public static boolean calculateSuccess(List<ProgressEntry> entries) {

		boolean success = !Iterables.any(entries, new ProgressHelper.IsErrorPredicate());
		return success;
	}

	private static class IsErrorPredicate implements Predicate<ProgressEntry> {

		@Override
		public boolean apply(ProgressEntry input) {
			return input != null && Status.ERROR == input.getStatus();
		}
	}
}
