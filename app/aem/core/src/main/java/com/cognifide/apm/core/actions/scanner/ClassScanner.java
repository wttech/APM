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
package com.cognifide.apm.core.actions.scanner;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Utility class for searching classes in specified bundle
 * If bundle context is provided it can also support fragment bundles
 */
@Slf4j
public class ClassScanner {

	private final Bundle bundle;

	private final BundleContext context;

	public ClassScanner(Bundle bundle) {
		this(bundle, null);
	}

	public ClassScanner(Bundle bundle, BundleContext context) {
		this.bundle = bundle;
		this.context = context;
	}

	public List<Class<?>> findClasses(String packageName) {
		@SuppressWarnings("unchecked") final Enumeration<URL> classUrls = bundle
				.findEntries(packageName.replace('.', '/'), "*.class", true);
		final String bundleName = bundle.getSymbolicName();
		final ArrayList<Class<?>> classes = new ArrayList<>();

		if (classUrls == null) {
			log.warn("No classes found in bundle: {}", bundleName);
		} else {
			while (classUrls.hasMoreElements()) {
				final URL url = classUrls.nextElement();
				final String className = toClassName(url);

				try {
					if (BundleUtils.isFragment(bundle)) {
						if (context == null) {
							log.warn("Cannot load class from fragment bundle {} if context is unspecified",
									bundleName);
						}

						final Bundle hostBundle = BundleUtils.getHostBundle(context, bundle);

						if (hostBundle == null) {
							log.warn("Cannot find host bundle for {}", bundleName);
						} else {
							classes.add(hostBundle.loadClass(className));
						}
					} else {
						classes.add(bundle.loadClass(className));
					}
				} catch (ClassNotFoundException e) {
					log.warn("Unable to load class", e);
				}
			}
		}

		return classes;
	}

	public List<Class<?>> findClasses(List<String> packages, Class<? extends Annotation> annotation) {
		ArrayList<Class<?>> classes = new ArrayList<>();

		for (String packageName : packages) {
			for (Class<?> clazz : findClasses(packageName)) {
				if (clazz != null && (annotation == null || clazz.isAnnotationPresent(annotation))) {
					classes.add(clazz);
				}
			}
		}

		return classes;
	}

	public List<Class<?>> findClasses(String header, Class<? extends Annotation> annotation) {
		return findClasses(parsePackagesFromHeader(header), annotation);
	}

	public List<Class<?>> findClasses(Class<? extends Annotation> annotation) {
		return findClasses(Collections.singletonList("/"), annotation);
	}

	public List<Class<?>> findClasses() {
		return findClasses(Collections.singletonList("/"), null);
	}

	private String toClassName(URL url) {
		final String f = url.getFile();
		final String cn = f.substring(1, f.length() - ".class".length());
		return cn.replace('/', '.');
	}

	private List<String> parsePackagesFromHeader(String header) {
		String values = PropertiesUtil.toString(bundle.getHeaders().get(header), null);
		if (values == null) {
			return Collections.emptyList();
		}
		String[] packages = StringUtils.deleteWhitespace(values).split(";");

		return Arrays.asList(packages);
	}
}