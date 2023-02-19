/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2018 Wunderman Thompson Technology
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
package com.cognifide.apm.core.progress;

import com.cognifide.apm.api.services.ExecutionResult.Entry;
import com.cognifide.apm.core.logger.ProgressEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.List;

public final class ProgressHelper {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private ProgressHelper() {
  }

  public static List<ProgressEntry> fromJson(String executionSummaryJson) {
    return Arrays.asList(GSON.fromJson(executionSummaryJson, ProgressEntry[].class));
  }

  public static String toJson(List<Entry> entries) {
    return GSON.toJson(entries.toArray());
  }

  public static String toJson(Entry entry) {
    return GSON.toJson(entry);
  }
}
