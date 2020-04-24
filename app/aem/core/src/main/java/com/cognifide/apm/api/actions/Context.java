package com.cognifide.apm.api.actions;

import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingPolicy;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlManager;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public interface Context {

  ValueFactory getValueFactory() throws RepositoryException;

  Authorizable getCurrentAuthorizable() throws ActionExecutionException;

  Authorizable getCurrentAuthorizableIfExists();

  Group getCurrentGroup() throws ActionExecutionException;

  User getCurrentUser() throws ActionExecutionException;

  void clearCurrentAuthorizable();

  AccessControlManager getAccessControlManager();

  AuthorizableManager getAuthorizableManager();

  SessionSavingPolicy getSavingPolicy();

  JackrabbitSession getSession();

  void setCurrentAuthorizable(Authorizable currentAuthorizable);

}
