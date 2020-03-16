/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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

package com.cognifide.apm.tools

import com.cognifide.cq.cqsm.api.executors.Mode
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.api.scripts.ExecutionMode
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import com.cognifide.cq.cqsm.api.scripts.ScriptManager
import com.cognifide.cq.cqsm.core.scripts.ScriptFilters.filterByExecutionMode
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper.getResourceResolverForService
import org.apache.jackrabbit.vault.packaging.InstallContext
import org.apache.jackrabbit.vault.packaging.InstallHook
import org.apache.jackrabbit.vault.packaging.PackageException
import org.apache.sling.api.resource.ResourceResolverFactory
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.FrameworkUtil
import org.osgi.framework.ServiceReference
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ApmInstallHook : InstallHook {

    private val logger: Logger = LoggerFactory.getLogger(ApmInstallHook::class.java)

    private var currentBundle: Bundle
    private var bundleContext: BundleContext

    init {
        currentBundle = FrameworkUtil.getBundle(this.javaClass)
                ?: throw IllegalStateException("The class ${this.javaClass} was not loaded through a bundle classloader")

        bundleContext = currentBundle.bundleContext
                ?: throw IllegalStateException("Could not get bundle context for bundle $currentBundle")
    }

    override fun execute(context: InstallContext) {
        if (context.phase == InstallContext.Phase.INSTALLED) {
            installScripts()
        }
    }

    private fun installScripts() {
        val resolverFactory = getService(ResourceResolverFactory::class.java)
        val scriptFinder = getService(ScriptFinder::class.java)
        val scriptManager = getService(ScriptManager::class.java)

        try {
            getResourceResolverForService(resolverFactory).use { resolver ->
                scriptFinder.findAll(filterByExecutionMode(ExecutionMode.ON_HOOK), resolver).forEach { script ->
                    val progress: Progress = scriptManager.process(script, Mode.AUTOMATIC_RUN, resolver)
                    logStatus(script.path, progress.isSuccess)
                }
            }
        } catch (e: Exception) {
            throw PackageException("Could not run scripts", e)
        }
    }

    private fun <T> getService(clazz: Class<T>): T {
        val serviceReference: ServiceReference<T> = bundleContext.getServiceReference(clazz)
                ?: throw PackageException("Could not find service ${clazz.name} in OSGI service registry")
        return bundleContext.getService(serviceReference)
                ?: throw PackageException("Could not receive instance of ${clazz.name}")
    }

    private fun logStatus(scriptPath: String, success: Boolean) {
        if (success) {
            logger.info("Script successfully executed: $scriptPath")
        } else {
            throw PackageException("Script cannot be executed properly: $scriptPath")
        }
    }
}