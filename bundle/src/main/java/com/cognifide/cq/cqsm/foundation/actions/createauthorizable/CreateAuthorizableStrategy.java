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
package com.cognifide.cq.cqsm.foundation.actions.createauthorizable;

import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.utils.AuthorizablesUtils;
import com.cognifide.cq.cqsm.foundation.RandomPasswordGenerator;
import com.cognifide.cq.cqsm.foundation.actions.MockPrincipal;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public enum CreateAuthorizableStrategy {

	GROUP {
		@Override
		public Group create(final String id, final String password, final String path, final Context context,
				final ActionResult actionResult, boolean simulate) throws RepositoryException {
			final MockPrincipal namePrincipal = new MockPrincipal(id);
			Group group;
			if (!simulate) {
				group = AuthorizablesUtils.createGroup(context, id, namePrincipal, path);
			} else {
				group = AuthorizablesUtils.createMockGroup(context, id);
			}

			actionResult.logMessage("Group with id: " + id + " created");
			return group;
		}
	},

	USER {
		@Override
		public User create(String id, String password, String path, Context context,
				ActionResult actionResult, boolean simulate) throws RepositoryException {
			final RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
			final MockPrincipal namePrincipal = new MockPrincipal(id);
			User user;
			if (!simulate) {
				user = AuthorizablesUtils.createUser(
						context, id, StringUtils.isBlank(password)
								? randomPasswordGenerator.getRandomPassword() : password,
						namePrincipal, path);
			} else {
				user = AuthorizablesUtils.createMockUser(context, id);
			}

			actionResult.logMessage("User with id: " + id + " created");
			return user;
		}
	},

	SYSTEM_USER {
		@Override
		public User create(String id, String password, String path, Context context,
				ActionResult actionResult, boolean simulate) throws RepositoryException {
			User user;
			if (!simulate) {
				user = AuthorizablesUtils.createSystemUser(context, id, path);
			} else {
				user = AuthorizablesUtils.createMockUser(context, id);
			}

			actionResult.logMessage("System user with id: " + id + " created");
			return user;
		}
	};

	public abstract Authorizable create(final String id, final String password, final String path,
			final Context context, final ActionResult actionResult, boolean simulate)
			throws RepositoryException;

}
