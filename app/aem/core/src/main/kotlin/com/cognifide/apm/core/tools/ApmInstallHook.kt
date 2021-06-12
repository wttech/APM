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

import com.cognifide.apm.api.scripts.LaunchEnvironment
import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ExecutionMode
import com.cognifide.apm.api.services.ExecutionResult
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.apm.core.scripts.ScriptFilters.onInstall
import com.cognifide.apm.core.scripts.ScriptFilters.onInstallIfModified
import com.cognifide.apm.core.services.ModifiedScriptFinder
import com.cognifide.apm.core.services.event.ApmEvent
import com.cognifide.apm.core.services.event.EventManager
import com.cognifide.apm.core.utils.InstanceTypeProvider
import com.cognifide.apm.core.utils.sling.SlingHelper
import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener
import org.apache.jackrabbit.vault.packaging.InstallContext
import org.apache.jackrabbit.vault.packaging.PackageException
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ApmInstallHook : OsgiAwareInstallHook() {

    private val logger: Logger = LoggerFactory.getLogger(ApmInstallHook::class.java)

    override fun execute(context: InstallContext) {
        if (context.phase == InstallContext.Phase.INSTALLED) {
            val currentEnvironment = getCurrentEnvironment()
            val currentHook = getCurrentHook(context)

            handleScripts(context, currentEnvironment, currentHook)
        }
    }

    private fun handleScripts(context: InstallContext, currentEnvironment: LaunchEnvironment, currentHook: String) {
        val resolverFactory = getService(ResourceResolverFactory::class.java)

        try {
            SlingHelper.operateTraced(resolverFactory) { resolver ->
                executeScripts(context, currentEnvironment, currentHook, resolver)
            }
            val eventManager = getService(EventManager::class.java)
            eventManager.trigger(ApmEvent.InstallHookExecuted(currentHook))
        } catch (e: Exception) {
            throw PackageException("Could not run scripts", e)
        }
    }

    private fun executeScripts(context: InstallContext, currentEnvironment: LaunchEnvironment, currentHook: String, resolver: ResourceResolver) {
        context.options.listener.onMessage(ProgressTrackerListener.Mode.TEXT, "Installing APM scripts...", "")
        val scriptManager = getService(ScriptManager::class.java)
        val scriptFinder = getService(ScriptFinder::class.java)
        val modifiedScriptFinder = getService(ModifiedScriptFinder::class.java)

        val scripts = mutableListOf<Script>()
        scripts.addAll(scriptFinder.findAll(onInstall(currentEnvironment, currentHook), resolver))
        scripts.addAll(modifiedScriptFinder.findAll(onInstallIfModified(currentEnvironment, currentHook), resolver))
        scripts.forEach { script ->
            context.options.listener.onMessage(ProgressTrackerListener.Mode.TEXT, "", script.path)
            val result: ExecutionResult = scriptManager.process(script, ExecutionMode.AUTOMATIC_RUN, resolver)
            logStatus(script.path, result)
        }
    }

    private fun getCurrentHook(context: InstallContext): String {
        val properties = context.`package`?.metaInf?.properties ?: Properties()
        val hookPropertyKey = properties.entries.asSequence()
                .filter { entry -> entry.value == this::class.java.name }
                .map { entry -> entry.key as String }
                .firstOrNull() ?: ""
        val hookRegex = Regex("installhook\\.(\\w+)\\.class")
        val result = hookRegex.matchEntire(hookPropertyKey)
        return result?.groups?.get(1)?.value ?: ""
    }

    private fun getCurrentEnvironment(): LaunchEnvironment {
        val instanceTypeProvider = getService(InstanceTypeProvider::class.java)
        return if (instanceTypeProvider.isOnAuthor) LaunchEnvironment.AUTHOR else LaunchEnvironment.PUBLISH
    }

    private fun logStatus(scriptPath: String, result: ExecutionResult) {
        if (result.isSuccess) {
            logger.info("Script successfully executed: $scriptPath")
        } else {
            throw PackageException("Script cannot be executed properly: $scriptPath")
        }
    }
}