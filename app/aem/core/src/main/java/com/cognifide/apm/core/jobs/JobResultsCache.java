/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
package com.cognifide.apm.core.jobs;

import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.core.Property;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(
    service = JobResultsCache.class,
    property = {
        Property.DESCRIPTION + "APM Job results holder service",
        Property.VENDOR
    }
)
public class JobResultsCache {

  private static final long DEFAULT_EXPIRATION_TIME = 10;

  private Cache<String, ExecutionSummary> cache;

  @Activate
  public void activate() {
    cache = CacheBuilder.newBuilder().expireAfterWrite(DEFAULT_EXPIRATION_TIME, TimeUnit.MINUTES).build();
  }

  public void put(String id, ExecutionSummary executionSummary) {
    cache.put(id, executionSummary);
  }

  public ExecutionSummary get(String id) {
    return cache.getIfPresent(id);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ExecutionSummary implements Serializable {

    private final boolean finished;
    private final ExecutionResult result;
    private final String path;

    public static ExecutionSummary running() {
      return new ExecutionSummary(false, null, null);
    }

    public static ExecutionSummary finished(ExecutionResult result, String path) {
      return new ExecutionSummary(true, result, path);
    }

    public boolean isFinished() {
      return finished;
    }

    public ExecutionResult getResult() {
      return result;
    }

    public String getPath() {
      return path;
    }
  }
}
