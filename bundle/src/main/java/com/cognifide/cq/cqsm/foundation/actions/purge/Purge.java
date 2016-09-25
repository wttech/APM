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
package com.cognifide.cq.cqsm.foundation.actions.purge;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionResult;
import com.cognifide.cq.cqsm.api.actions.interfaces.ResourceResolvable;
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.api.logger.Status;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.foundation.actions.removeall.RemoveAll;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;

import javax.jcr.RepositoryException;

public class Purge implements Action, ResourceResolvable {

	private static final Logger LOGGER = LoggerFactory.getLogger(Purge.class);

	private static final String PERMISSION_STORE_PATH = "/jcr:system/rep:permissionStore/crx.default/";

	private final String path;

	private ResourceResolver resourceResolver;

	public Purge(final String path) {
		this.path = path;
	}

	@Override
	public ActionResult simulate(Context context) {
		return process(context, false);
	}

	@Override
	public ActionResult execute(final Context context) {
		return process(context, true);
	}

	private ActionResult process(final Context context, boolean execute) {
		ActionResult actionResult = new ActionResult();
		try {
			Authorizable authorizable = context.getCurrentAuthorizable();
			actionResult.setAuthorizable(authorizable.getID());
			LOGGER.info(String.format("Purging privileges for authorizable with id = %s under path = %s",
					authorizable.getID(), path));
			if (execute) {
				purge(context, actionResult);
			}
			actionResult.logMessage("Purged privileges for " + authorizable.getID() + " on " + path);
		} catch (RepositoryException | ActionExecutionException e) {
			actionResult.logError(MessagingUtils.createMessage(e));
		}

		return actionResult;
	}

	private void purge(final Context context, final ActionResult actionResult)
			throws RepositoryException, ActionExecutionException {
		Iterator<Resource> iterator = getPermissions(context);
		String normalizedPath = normalizePath(path);
		while (iterator.hasNext()) {
			String parentPath = iterator.next().getValueMap()
					.get(PermissionConstants.REP_ACCESS_CONTROLLED_PATH, String.class);
			String normalizedParentPath = normalizePath(parentPath);
			if (StringUtils.startsWith(normalizedParentPath, normalizedPath)) {
				RemoveAll removeAll = new RemoveAll(parentPath);
				ActionResult removeAllResult = removeAll.execute(context);
				if (Status.ERROR.equals(removeAllResult.getStatus())) {
					actionResult.logError(removeAllResult);
				}
			}
		}
	}

	private Iterator<Resource> getPermissions(Context context)
			throws ActionExecutionException, RepositoryException {
		Iterator<Resource> iterator = Collections.emptyIterator();
		Resource resource = resourceResolver
				.getResource(PERMISSION_STORE_PATH + context.getCurrentAuthorizable().getID());
		if (resource != null) {
			iterator = resource.listChildren();
		}

		return iterator;
	}

	private String normalizePath(String path) {
		return path + (path.endsWith("/") ? "" : "/");
	}

	@Override
	public boolean isGeneric() {
		return false;
	}

	@Override
	public void setResourceResolver(ResourceResolver resolver) {
		this.resourceResolver = resolver;
	}

	@Override
	public void setResourceResolverFactory(ResourceResolverFactory factory) {
	}

}
