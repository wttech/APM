package com.cognifide.apm.api.actions;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface SessionSavingPolicy {

  void setMode(SessionSavingMode mode);

  void save(Session session, SessionSavingMode sessionSavingMode) throws RepositoryException;
}
