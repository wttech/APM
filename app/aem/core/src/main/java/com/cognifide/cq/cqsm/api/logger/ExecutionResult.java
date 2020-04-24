package com.cognifide.cq.cqsm.api.logger;

import java.util.List;

public interface ExecutionResult {

  List<ProgressEntry> getEntries();

  boolean isSuccess();

  ProgressEntry getLastError();

  String getExecutor();
}
