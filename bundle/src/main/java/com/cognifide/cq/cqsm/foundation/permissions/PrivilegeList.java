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
package com.cognifide.cq.cqsm.foundation.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

public enum PrivilegeList {

	READ("READ", Privilege.JCR_READ),

	MODIFY("MODIFY", Privilege.JCR_MODIFY_PROPERTIES, Privilege.JCR_LOCK_MANAGEMENT,
			Privilege.JCR_VERSION_MANAGEMENT),

	MODIFY_PAGE("MODIFY_PAGE", Privilege.JCR_REMOVE_NODE, Privilege.JCR_REMOVE_CHILD_NODES,
			Privilege.JCR_NODE_TYPE_MANAGEMENT, Privilege.JCR_ADD_CHILD_NODES),

	CREATE("CREATE", Privilege.JCR_ADD_CHILD_NODES, Privilege.JCR_NODE_TYPE_MANAGEMENT),

	DELETE("DELETE", Privilege.JCR_REMOVE_NODE, Privilege.JCR_REMOVE_CHILD_NODES),

	REPLICATE("REPLICATE", "crx:replicate"),

	ALL("ALL", Privilege.JCR_READ, Privilege.JCR_WRITE, Privilege.JCR_LOCK_MANAGEMENT,
			Privilege.JCR_VERSION_MANAGEMENT, Privilege.JCR_NODE_TYPE_MANAGEMENT, "crx:replicate"),

	READ_ACL("READ_ACL", Privilege.JCR_READ_ACCESS_CONTROL),

	MODIFY_ACL("MODIFY_ACL", Privilege.JCR_MODIFY_ACCESS_CONTROL),

	DELETE_CHILD_NODES("DELETE_CHILD_NODES", Privilege.JCR_REMOVE_CHILD_NODES),

	JCR_READ("jcr:read", Privilege.JCR_READ),

	JCR_MODIFY_PROPERTIES("jcr:modifyProperties", Privilege.JCR_MODIFY_PROPERTIES),

	JCR_ADD_CHILD_NODES("jcr:addChildNodes", Privilege.JCR_ADD_CHILD_NODES),

	JCR_REMOVE_NODE("jcr:removeNode", Privilege.JCR_REMOVE_NODE),

	JCR_REMOVE_CHILD_NODES("jcr:removeChildNodes", Privilege.JCR_REMOVE_CHILD_NODES),

	JCR_WRITE("jcr:write", Privilege.JCR_WRITE),

	JCR_READ_ACCESS_CONTROL("jcr:readAccessControl", Privilege.JCR_READ_ACCESS_CONTROL),

	JCR_MODIFY_ACCESS_CONTROL("jcr:modifyAccessControl", Privilege.JCR_MODIFY_ACCESS_CONTROL),

	JCR_LOCK_MANAGEMENT("jcr:lockManagement", Privilege.JCR_LOCK_MANAGEMENT),

	JCR_VERSION_MANAGEMENT("jcr:versionManagement", Privilege.JCR_VERSION_MANAGEMENT),

	JCR_NODE_TYPE_MANAGEMENT("jcr:nodeTypeManagement", Privilege.JCR_NODE_TYPE_MANAGEMENT),

	JCR_RETENTION_MANAGEMENT("jcr:retentionManagement", Privilege.JCR_RETENTION_MANAGEMENT),

	JCR_LIFECYCLE_MANAGEMENT("jcr:lifecycleManagement", Privilege.JCR_LIFECYCLE_MANAGEMENT),

	JCR_ALL("jcr:all", Privilege.JCR_ALL),

	CRX_REPLICATE("crx:replicate", "crx:replicate");

	private final String title;

	private final List<String> privileges;

	PrivilegeList(String title, String... privileges) {
		this.title = title;
		this.privileges = new ArrayList<>();
		this.privileges.addAll(Arrays.asList(privileges));
	}

	public static PrivilegeList getFromTitle(String title) {
		for (PrivilegeList privilegeList : PrivilegeList.values()) {
			if (privilegeList.getTitle().equalsIgnoreCase(title)) {
				return privilegeList;
			}
		}

		throw new IllegalArgumentException(String.format("Provided %s permission is not recognized", title));
	}

	public String getTitle() {
		return title;
	}

	public List<Privilege> createPrivileges(AccessControlManager accessControlManager)
			throws RepositoryException {
		final List<Privilege> result = new ArrayList<>();
		for (String privilege : privileges) {
			result.add(accessControlManager.privilegeFromName(privilege));
		}
		return result;
	}

}
