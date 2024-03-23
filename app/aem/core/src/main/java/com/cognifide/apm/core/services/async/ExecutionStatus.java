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
package com.cognifide.apm.core.services.async;

import com.cognifide.apm.api.services.ExecutionResult;
import com.cognifide.apm.core.ui.utils.DateFormatter;
import java.util.Calendar;
import java.util.List;

public abstract class ExecutionStatus {

  private final String status;

  private ExecutionStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public static class Running extends ExecutionStatus {

    public Running() {
      super("running");
    }
  }

  public static class Unknown extends ExecutionStatus {

    public Unknown() {
      super("unknown");
    }
  }

  protected static class Finished extends ExecutionStatus {

    private final String path;

    private final long timestamp;

    private final String formattedDate;

    private final List<ExecutionResult.Entry> entries;

    public Finished(String path, Calendar startTime, List<ExecutionResult.Entry> entries) {
      super("finished");
      this.path = path;
      this.timestamp = startTime.getTimeInMillis();
      this.formattedDate = DateFormatter.format(startTime);
      this.entries = entries;
    }

    public String getPath() {
      return path;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public String getFormattedDate() {
      return formattedDate;
    }

    public List<ExecutionResult.Entry> getEntries() {
      return entries;
    }
  }

  public static class Successful extends Finished {

    public Successful(String path, Calendar startTime, List<ExecutionResult.Entry> entries) {
      super(path, startTime, entries);
    }
  }

  public static class Failed extends Finished {

    private final ExecutionResult.Entry error;

    public Failed(String path, Calendar startTime, List<ExecutionResult.Entry> entries, ExecutionResult.Entry error) {
      super(path, startTime, entries);
      this.error = error;
    }

    public ExecutionResult.Entry getError() {
      return error;
    }
  }
}
