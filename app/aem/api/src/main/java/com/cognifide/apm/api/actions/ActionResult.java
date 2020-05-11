package com.cognifide.apm.api.actions;

import com.cognifide.apm.api.status.Status;
import java.util.List;

public interface ActionResult {

  void logMessage(String message);

  void logWarning(String warning);

  void logError(String error);

  List<Message> getMessages();

  void setAuthorizable(String authorizable);

  ActionResult merge(ActionResult... actionResult);

  String getAuthorizable();

  Status getStatus();
}
