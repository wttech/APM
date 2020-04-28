package com.cognifide.apm.api.services;

import com.cognifide.apm.api.status.Status;
import java.util.List;

public interface ExecutionResult {

  List<Entry> getEntries();

  boolean isSuccess();

  Entry getLastError();

  String getExecutor();

  interface Entry {

    String getAuthorizable();

    String getCommand();

    List<String> getMessages();

    List<String> getParameters();

    Status getStatus();
  }
}
