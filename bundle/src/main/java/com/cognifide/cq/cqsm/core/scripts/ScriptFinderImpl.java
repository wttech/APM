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

import com.google.common.collect.ImmutableList;

import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.core.Cqsm;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Script Finder Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)})
public class ScriptFinderImpl implements ScriptFinder {

	private static final String ROOT_PATH = "/conf/apm/scripts";

	private static final String SCRIPT_PATH = ROOT_PATH + "/cqsmImport";

	private static final String INCLUDE_PATH = ROOT_PATH + "/cqsmInclude";

	@Override
	public List<Script> findAll(Predicate filter, ResourceResolver resolver) {
		final List<Script> scripts = findAll(resolver);
		CollectionUtils.filter(scripts, filter);
		return scripts;
	}

	@Override
	public List<Script> findAll(ResourceResolver resolver) {
		return findAll(true, resolver);
	}

	private List<Script> findAll(boolean skipIgnored, ResourceResolver resourceResolver) {
		List<Script> scripts = new LinkedList<>();
		for (String path : getSearchPaths()) {
			Resource root = resourceResolver.getResource(path);
			if (root != null) {
				Iterator<Resource> children = root.listChildren();
				scripts.addAll(getScripts(children, skipIgnored));
			}
		}
		return scripts;
	}

	@Override
	public Script find(String path, ResourceResolver resolver) {
		return find(path, true, resolver);
	}

	@Override
	public Script find(String path, boolean skipIgnored, ResourceResolver resolver) {
		Script result = null;
		if (StringUtils.isNotEmpty(path) && (!skipIgnored || isNotIgnoredPath(path))) {
			Resource resource = null;
			if (path.contains(ROOT_PATH)) {
				resource = resolver.getResource(path);
			}
			if (resource == null) {
				path = path.startsWith("/") ? path.substring(1) : path;
				for (String searchPath : getSearchPaths()) {
					resource = resolver.getResource(searchPath + "/" + path);
					if (resource != null) {
						break;
					}
				}
			}
			if (resource != null) {
				result = resource.adaptTo(ScriptImpl.class);
			}
		}
		return result;
	}

	private List<Script> getScripts(Iterator<Resource> scriptIterator, boolean skipIgnored) {
		List<Script> scripts = new LinkedList<>();
		while (scriptIterator.hasNext()) {
			Resource resource = scriptIterator.next();
			if (!skipIgnored || isNotIgnoredPath(resource.getPath())) {
				Script script = resource.adaptTo(ScriptImpl.class);
				if (script != null) {
					scripts.add(script);
				}
			}
		}
		return scripts;
	}

	private boolean isNotIgnoredPath(String path) {
		return !ScriptManager.FILE_FOR_EVALUATION.equals(FilenameUtils.getBaseName(path));
	}

	private List<String> getSearchPaths() {
		return ImmutableList.<String>builder() //
				.add(SCRIPT_PATH) //
				.add(INCLUDE_PATH) //
				.build();
	}
}
