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

package com.cognifide.apm.core.tools

import com.cognifide.apm.api.scripts.LaunchEnvironment
import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ExecutionMode
import com.cognifide.apm.api.services.ExecutionResult
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.api.services.ScriptManager
import com.cognifide.apm.core.scripts.ScriptFilters.*
import com.cognifide.apm.core.services.ModifiedScriptFinder
import com.cognifide.apm.core.services.version.VersionService
import com.cognifide.apm.core.utils.InstanceTypeProvider
import com.cognifide.apm.core.utils.sling.SlingHelper.getResourceResolverForService
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

            handleScripts(currentEnvironment, currentHook)
        }
    }

    private fun handleScripts(currentEnvironment: LaunchEnvironment, currentHook: String) {
        val resolverFactory = getService(ResourceResolverFactory::class.java)
        val scriptFinder = getService(ScriptFinder::class.java)

        try {
            getResourceResolverForService(resolverFactory).use { resolver ->
                executeScripts(currentEnvironment, currentHook, resolver)
                applyChecksum(scriptFinder, resolver)
            }
        } catch (e: Exception) {
            throw PackageException("Could not run scripts", e)
        }
    }

    private fun executeScripts(currentEnvironment: LaunchEnvironment, currentHook: String, resolver: ResourceResolver) {
        val scriptManager = getService(ScriptManager::class.java)
        val scriptFinder = getService(ScriptFinder::class.java)
        val modifiedScriptFinder = getService(ModifiedScriptFinder::class.java)

        val scripts = mutableListOf<Script>()
        scripts.addAll(scriptFinder.findAll(onInstall(currentEnvironment, currentHook), resolver))
        scripts.addAll(modifiedScriptFinder.findAll(onInstallIfModified(currentEnvironment, currentHook), resolver))
        scripts.forEach { script ->
            val result: ExecutionResult = scriptManager.process(script, ExecutionMode.AUTOMATIC_RUN, resolver)
            logStatus(script.path, result.isSuccess)
        }
    }

    private fun applyChecksum(scriptFinder: ScriptFinder, resolver: ResourceResolver) {
        val scripts = scriptFinder.findAll(noChecksum(), resolver)
        if (scripts.isNotEmpty()) {
            val versionService = getService(VersionService::class.java)
            versionService.updateVersionIfNeeded(resolver, *scripts.toTypedArray())
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

    private fun logStatus(scriptPath: String, success: Boolean) {
        if (success) {
            logger.info("Script successfully executed: $scriptPath")
        } else {
            throw PackageException("Script cannot be executed properly: $scriptPath")
        }
    }
}