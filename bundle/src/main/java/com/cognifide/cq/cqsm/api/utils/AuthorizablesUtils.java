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
package com.cognifide.cq.cqsm.api.utils;

import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.foundation.actions.MockGroup;
import com.cognifide.cq.cqsm.foundation.actions.MockUser;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Iterator;

import javax.jcr.RepositoryException;

/**
 * This class provides means of controlling groups and users to be used by {@link Context} : testing presence,
 * creating and deleting. It contains functionality originally placed in {@link Context}, but was extracted
 * to follow Single Responsibility Principle (at least a bit more). Further changes are possible.
 */
public final class AuthorizablesUtils {

	private AuthorizablesUtils() {
	}

	// *************************** AUTH UTILS **************************************
	public static Authorizable getAuthorizableIfExists(Context context, String id)
			throws RepositoryException {
		if (checkIfRemoved(context, id)) {
			return null;
		}
		Authorizable authorizable = context.getAuthorizables().get(id);

		if (authorizable == null) {
			authorizable = context.getUserManager().getAuthorizable(id);
		}

		return authorizable;
	}

	public static Authorizable getAuthorizable(Context context, String id)
			throws ActionExecutionException, RepositoryException {
		if (checkIfRemoved(context, id)) {
			throw new ActionExecutionException("Authorizable with id " + id + " not found");
		}

		Authorizable authorizable = context.getAuthorizables().get(id);

		if (authorizable == null) {
			authorizable = context.getUserManager().getAuthorizable(id);
		}

		if (authorizable == null) {
			throw new ActionExecutionException("Authorizable with id " + id + " not found");
		}

		context.getAuthorizables().put(id, authorizable);
		return authorizable;
	}

	public static void markAuthorizableAsRemoved(Context context, Authorizable authorizable)
			throws RepositoryException {
		context.getRemovedAuthorizables().add(authorizable.getID());
	}

	// *************************** GROUP UTILS *************************************
	public static Group getGroupIfExists(Context context, String id)
			throws RepositoryException, ActionExecutionException {
		if (checkIfRemoved(context, id)) {
			return null;
		}

		Authorizable authorizable = context.getAuthorizables().get(id);

		if (authorizable == null) {
			authorizable = context.getUserManager().getAuthorizable(id);
		}

		if (authorizable == null) {
			return null;
		}

		if (authorizable instanceof User) {
			throw new ActionExecutionException(
					"Authorizable with id " + id + " exists but is a user not a group");
		}

		context.getAuthorizables().put(id, authorizable);
		return (Group) authorizable;
	}

	public static Group getGroup(Context context, String id)
			throws ActionExecutionException, RepositoryException {
		if (checkIfRemoved(context, id)) {
			throw new ActionExecutionException("Group with id " + id + " not found");
		}

		Authorizable authorizable = context.getAuthorizables().get(id);

		if (authorizable == null) {
			authorizable = context.getUserManager().getAuthorizable(id);
		}

		if (authorizable == null) {
			throw new ActionExecutionException("Group with id " + id + " not found");
		}

		if (authorizable instanceof User) {
			throw new ActionExecutionException(
					"Authorizable with id " + id + " exists but is a user not a group");
		}

		context.getAuthorizables().put(id, authorizable);
		return (Group) authorizable;
	}

	public static Group createGroup(Context context, String id, Principal namePrincipal, String path)
			throws RepositoryException {
		Group group = context.getUserManager().createGroup(id, namePrincipal, path);
		context.getAuthorizables().put(id, group);
		context.getRemovedAuthorizables().remove(id);
		return group;
	}

	public static Group createMockGroup(Context context, String id) {
		Group group = new MockGroup(id);
		context.getAuthorizables().put(id, group);
		context.getRemovedAuthorizables().remove(id);
		return group;
	}

	public static void removeGroup(Context context, Group group) throws RepositoryException {
		context.getAuthorizables().remove(group.getID());
		group.remove();
	}

	// *************************** USER UTILS **************************************
	public static User getUserIfExists(Context context, String id)
			throws ActionExecutionException, RepositoryException {
		if (checkIfRemoved(context, id)) {
			return null;
		}

		Authorizable authorizable = context.getAuthorizables().get(id);

		if (authorizable == null) {
			authorizable = context.getUserManager().getAuthorizable(id);
		}

		if (authorizable == null) {
			return null;
		}

		if (authorizable instanceof Group) {
			throw new ActionExecutionException(
					"Authorizable with id " + id + " exists but is a group not a user");
		}

		context.getAuthorizables().put(id, authorizable);
		return (User) authorizable;
	}

	public static User getUser(Context context, String id)
			throws ActionExecutionException, RepositoryException {
		if (checkIfRemoved(context, id)) {
			throw new ActionExecutionException("User with id " + id + " not found");
		}

		Authorizable authorizable = context.getAuthorizables().get(id);

		if (authorizable == null) {
			authorizable = context.getUserManager().getAuthorizable(id);
		}

		if (authorizable == null) {
			throw new ActionExecutionException("User with id " + id + " not found");
		}

		if (authorizable instanceof Group) {
			throw new ActionExecutionException(
					"Authorizable with id " + id + " exists but is a group not a user");
		}

		context.getAuthorizables().put(id, authorizable);
		return (User) authorizable;
	}

	public static User createUser(Context context, String id, String password, Principal namePrincipal,
			String path) throws RepositoryException {
		User user = context.getUserManager().createUser(id, password, namePrincipal, path);
		context.getAuthorizables().put(id, user);
		context.getRemovedAuthorizables().remove(id);
		return user;
	}

	public static User createSystemUser(Context context, String id, String path) throws RepositoryException {
		try {
			UserManager userManager = context.getUserManager();
			Method method = userManager.getClass().getMethod("createSystemUser", String.class, String.class);
			User user = (User) method.invoke(userManager, id, path);
			context.getAuthorizables().put(id, user);
			context.getRemovedAuthorizables().remove(id);
			return user;
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RepositoryException(e);
		}
	}

	public static User createMockUser(Context context, String id) {
		User user = new MockUser(id);
		context.getAuthorizables().put(id, user);
		context.getRemovedAuthorizables().remove(id);
		return user;
	}

	public static void removeUser(Context context, User user) throws RepositoryException {
		Iterator<Group> groups = user.memberOf();
		while (groups.hasNext()) {
			groups.next().removeMember(user);
		}
		context.getAuthorizables().remove(user.getID());
		user.remove();
	}

	// *************************** LOCAL UTILS **************************************
	private static boolean checkIfRemoved(Context context, String id) {
		return context.getRemovedAuthorizables().contains(id);
	}

}
