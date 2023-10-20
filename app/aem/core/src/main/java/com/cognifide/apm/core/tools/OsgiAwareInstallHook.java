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

package com.cognifide.apm.core.tools

import org.apache.jackrabbit.vault.packaging.InstallHook
import org.apache.jackrabbit.vault.packaging.PackageException
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.FrameworkUtil
import org.osgi.framework.ServiceReference

abstract class OsgiAwareInstallHook : InstallHook {

    protected var currentBundle: Bundle
    protected var bundleContext: BundleContext

    init {
        currentBundle = FrameworkUtil.getBundle(this.javaClass)
            ?: throw IllegalStateException("The class ${this.javaClass} was not loaded through a bundle classloader")

        bundleContext = currentBundle.bundleContext
            ?: throw IllegalStateException("Could not get bundle context for bundle $currentBundle")
    }

    protected fun <T> getService(clazz: Class<T>): T {
        val serviceReference: ServiceReference<T> = bundleContext.getServiceReference(clazz)
            ?: throw PackageException("Could not find service ${clazz.name} in OSGI service registry")
        return bundleContext.getService(serviceReference)
            ?: throw PackageException("Could not receive instance of ${clazz.name}")
    }
}