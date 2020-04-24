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
package com.cognifide.cq.cqsm.core.scripts;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Checksum {

	private static final Logger LOG = LoggerFactory.getLogger(Checksum.class);

	private static final String CHECKSUM_STORAGE_PATH = "/var/cqsm/scripts/version";

	private final String filename;

	public Checksum(String filename) {
		this.filename = filename;
	}

	public String calculate(Resource resource) {
		String result = null;
		try {
			result = DigestUtils.md5Hex(resource.adaptTo(InputStream.class));
		} catch (IOException e) {
			LOG.error("Cannot read content of resource: " + resource.getPath(), e);
		}
		return result;
	}

	public String update(ResourceResolver resolver, Resource resource) {
		String value = calculate(resource);
		save(resolver, value);
		return value;
	}

	public void save(ResourceResolver resolver, String checksum) {
		try {
			Resource storage = getOrCreateChecksumStorage(resolver, CHECKSUM_STORAGE_PATH);
			storage.adaptTo(ModifiableValueMap.class).put(filename, checksum);
			resolver.commit();
		} catch (PersistenceException e) {
			LOG.error("Cannot update checksum for {} script", filename, e);
		}
	}

	public String load(ResourceResolver resolver) {
		Resource storage = getOrCreateChecksumStorage(resolver, CHECKSUM_STORAGE_PATH);
		return storage.adaptTo(ValueMap.class).get(filename, String.class);
	}

	private Resource getOrCreateChecksumStorage(ResourceResolver resolver, String path) {
		Resource storage = null;
		try {
			boolean autoCommit = true;
			storage = ResourceUtil.getOrCreateResource(resolver, path, JcrConstants.NT_UNSTRUCTURED,
					JcrConstants.NT_UNSTRUCTURED, autoCommit);
		} catch (PersistenceException e) {
			LOG.error("Cannot create storage for script checksum", e);
		}
		return storage;

	}
}
