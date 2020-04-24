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
package com.cognifide.cq.cqsm.foundation.permissions.utils;

import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;

public final class JackrabbitAccessControlListUtil {

	private JackrabbitAccessControlListUtil() {
	}

	public static JackrabbitAccessControlList getModifiableAcl(final AccessControlManager accessManager,
			final String path) throws RepositoryException {
		final JackrabbitAccessControlList acl = getAccessControlList(accessManager, path);
		if (null != acl) {
			return acl;
		}

		final JackrabbitAccessControlList applicableAcl = getApplicableAccessControlList(accessManager, path);
		if (null != applicableAcl) {
			return applicableAcl;
		}

		throw new AccessControlException("No modifiable ACL at " + path);
	}

	public static JackrabbitAccessControlList getApplicableAccessControlList(
			final AccessControlManager accessManager, final String path) throws RepositoryException {
		// find policies which may be applied to node indicated by path (may be treated as policy factory)
		final AccessControlPolicyIterator applicablePolicies = accessManager.getApplicablePolicies(path);
		while (applicablePolicies.hasNext()) {
			final AccessControlPolicy policy = applicablePolicies.nextAccessControlPolicy();
			if (policy instanceof JackrabbitAccessControlList) {
				return (JackrabbitAccessControlList) policy;
			}
		}
		return null;
	}

	public static JackrabbitAccessControlList getAccessControlList(final AccessControlManager accessManager,
			final String path) throws RepositoryException {
		final AccessControlPolicy[] existing = accessManager.getPolicies(path);
		for (final AccessControlPolicy policy : existing) {
			if (policy instanceof JackrabbitAccessControlList) {
				return (JackrabbitAccessControlList) policy;
			}
		}
		return null;
	}

}