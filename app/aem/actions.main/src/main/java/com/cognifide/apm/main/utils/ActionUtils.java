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
package com.cognifide.apm.main.utils;

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import java.util.Iterator;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;

public final class ActionUtils {

	public static final String ASSERTION_FAILED_MSG = "Assertion failed";

	private ActionUtils() {
	}

	/**
	 * Adding group to another group may result in cyclic relation. Let current group be the group where we
	 * want to add current authorizable to. If current authorizable contains group such that current group
	 * belongs to, then we prevent such operation.
	 *
	 * @param currentGroup   The group where we want to add current authorizable
	 * @param groupToBeAdded Authorizable we want to add
	 * @throws ActionExecutionException Throw exception, if adding operation results in cyclic relation
	 */
	public static void checkCyclicRelations(Group currentGroup, Group groupToBeAdded)
			throws ActionExecutionException {
		try {
			if (groupToBeAdded.getID().equals(currentGroup.getID())) {
				throw new ActionExecutionException(MessagingUtils.addingGroupToItself(currentGroup.getID()));
			}
			Iterator<Group> parents = currentGroup.memberOf();
			while (parents.hasNext()) {
				Group currentParent = parents.next();
				// Is added group among my parents?
				if (currentParent.getID().equals(groupToBeAdded.getID())) {
					throw new ActionExecutionException(MessagingUtils.cyclicRelationsForbidden(
							currentGroup.getID(), groupToBeAdded.getID()));
				}
				// ... and are its children among my parents?
				for (Iterator<Authorizable> children = groupToBeAdded.getMembers(); children.hasNext(); ) {
					Authorizable currentChild = children.next();
					if (currentParent.getID().equals(currentChild.getID())) {
						throw new ActionExecutionException(MessagingUtils.cyclicRelationsForbidden(
								currentChild.getID(), groupToBeAdded.getID()));
					}
				}
			}
		} catch (RepositoryException e) {
			throw new ActionExecutionException(MessagingUtils.createMessage(e));
		}
	}

	public static void logErrors(List<String> errors, ActionResult actionResult) {
		for (String error : errors) {
			actionResult.logError(error);
		}
	}
}
