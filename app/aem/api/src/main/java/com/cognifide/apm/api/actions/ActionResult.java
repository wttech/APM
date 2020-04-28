package com.cognifide.apm.api.actions;

import com.cognifide.apm.api.status.Status;

public interface ActionResult {

  void logMessage(String message);

  void logWarning(String warning);

  void logError(String error);

  void setAuthorizable(String authorizable);

  String getAuthorizable();

  Status getStatus();
}
