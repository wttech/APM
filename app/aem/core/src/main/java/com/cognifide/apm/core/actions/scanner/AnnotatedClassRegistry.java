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

import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * Aggregator for classes with specified annotation <p> When bundle is state is changed (added, removed,
 * modified), then it is executed class scanner which looks for classes with prefixes specified in bundle
 * header
 */
@Slf4j
public class AnnotatedClassRegistry {

  private final BundleTracker tracker;

  private final Map<Long, List<Class<?>>> classes = new ConcurrentHashMap<>();

  private final Class<? extends Annotation> annotationClass;

  private final String bundleHeader;

  private final BundleContext bundleContext;

  private final Set<RegistryChangedListener> listeners = new HashSet<>();

  public AnnotatedClassRegistry(final BundleContext bundleContext, final String bundleHeader,
      final Class<? extends Annotation> annotationClass) {
    this.bundleContext = bundleContext;
    this.bundleHeader = bundleHeader;
    this.annotationClass = annotationClass;

    this.tracker = new BundleTracker(bundleContext, Bundle.ACTIVE, new BundleTrackerCustomizer<Bundle>() {

      @Override
      public Bundle addingBundle(Bundle bundle, BundleEvent bundleEvent) {
        if (null != bundle.getHeaders().get(bundleHeader)) {
          registerClasses(bundle);
          return bundle;
        }
        return null;
      }

      @Override
      public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, Bundle bundle2) {
        //do nothing
      }

      @Override
      public void removedBundle(Bundle bundle, BundleEvent bundleEvent, Bundle bundle2) {
        unregisterClasses(bundle);
      }

    });
  }

  public void addChangeListener(RegistryChangedListener changeListener) {
    this.listeners.add(changeListener);
  }

  public List<Class<?>> getFlattenedClasses() {
    List<Class<?>> flattened = new ArrayList<>();
    for (Map.Entry<Long, List<Class<?>>> entry : classes.entrySet()) {
      flattened.addAll(entry.getValue());
    }

    return ImmutableList.copyOf(flattened);
  }

  private void registerClasses(Bundle bundle) {
    final List<Class<?>> scanned = new ClassScanner(bundle, bundleContext)
        .findClasses(bundleHeader, annotationClass);
    if (!scanned.isEmpty()) {
      classes.put(bundle.getBundleId(), scanned);
      notifyChangeListeners();
    }
    if (log.isDebugEnabled()) {
      log.debug("Adding classes ({}) from bundle: {}", scanned.size(), bundle.getSymbolicName());
    }
  }

  private void unregisterClasses(Bundle bundle) {
    final List<Class<?>> registered = classes.get(bundle.getBundleId());
    if (log.isDebugEnabled()) {
      log.debug("Removing classes ({}) from bundle: {}", registered.size(), bundle.getSymbolicName());
    }
    classes.remove(bundle.getBundleId());
    notifyChangeListeners();
  }

  private void notifyChangeListeners() {
    List<Class<?>> flattenedClasses = getFlattenedClasses();
    for (RegistryChangedListener listener : listeners) {
      listener.registryChanged(flattenedClasses);
    }
  }

  public void open() {
    tracker.open();
  }

  public void close() {
    tracker.close();
  }
}
