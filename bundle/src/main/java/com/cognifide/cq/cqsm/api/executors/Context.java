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
package com.cognifide.cq.cqsm.api.executors;

import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingPolicy;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlManager;

import lombok.Getter;
import lombok.Setter;

public final class Context {

	@Getter
	private final AccessControlManager accessControlManager;

	@Getter
	private final JackrabbitSession session;

	@Setter
	private Authorizable currentAuthorizable;

	@Getter
	private final SessionSavingPolicy savingPolicy;

	@Getter
	private final UserManager userManager;

	@Getter
	private final Map<String, Authorizable> authorizables = new HashMap<>();

	@Getter
	private final List<String> removedAuthorizables = new ArrayList<>();

	public Context(final JackrabbitSession session) throws RepositoryException {
		this.session = session;
		this.accessControlManager = session.getAccessControlManager();
		this.userManager = session.getUserManager();
		this.savingPolicy = new SessionSavingPolicy();
	}

	public ValueFactory getValueFactory() throws RepositoryException {
		return session.getValueFactory();
	}

	public Authorizable getCurrentAuthorizable() throws ActionExecutionException {
		if (currentAuthorizable == null) {
			throw new ActionExecutionException("Current authorizable not set.");
		}
		return currentAuthorizable;
	}

	public Authorizable getCurrentAuthorizableIfExists() {
		return currentAuthorizable;
	}

	public Group getCurrentGroup() throws ActionExecutionException {
		if (getCurrentAuthorizable() instanceof User) {
			throw new ActionExecutionException("Current authorizable is not a group");
		}
		return (Group) currentAuthorizable;
	}

	public User getCurrentUser() throws ActionExecutionException {
		if (getCurrentAuthorizable() instanceof Group) {
			throw new ActionExecutionException("Current authorizable is not a user");
		}
		return (User) currentAuthorizable;
	}

	public void clearCurrentAuthorizable() {
		this.currentAuthorizable = null;
	}
}