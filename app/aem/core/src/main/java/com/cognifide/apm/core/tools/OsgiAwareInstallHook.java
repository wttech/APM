/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.apm.core.tools;

import java.util.Optional;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public abstract class OsgiAwareInstallHook implements InstallHook {

  protected final Bundle currentBundle;

  protected final BundleContext bundleContext;

  public OsgiAwareInstallHook() {
    currentBundle = Optional.ofNullable(FrameworkUtil.getBundle(this.getClass()))
        .orElseThrow(() -> new IllegalStateException(String.format("The class %s was not loaded through a bundle classloader", this.getClass().getCanonicalName())));
    bundleContext = Optional.ofNullable(currentBundle.getBundleContext())
        .orElseThrow(() -> new IllegalStateException(String.format("Could not get bundle context for bundle %s", currentBundle)));
  }

  protected <T> T getService(Class<T> clazz) throws PackageException {
    ServiceReference<T> serviceReference = Optional.ofNullable(bundleContext.getServiceReference(clazz))
        .orElseThrow(() -> new PackageException(String.format("Could not find service %s in OSGI service registry", clazz.getCanonicalName())));
    return Optional.ofNullable(bundleContext.getService(serviceReference))
        .orElseThrow(() -> new PackageException(String.format("Could not receive instance of %s", clazz.getCanonicalName())));
  }
}
