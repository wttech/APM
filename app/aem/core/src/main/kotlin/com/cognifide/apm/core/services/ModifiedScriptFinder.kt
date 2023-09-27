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

package com.cognifide.apm.core.services

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.grammar.ReferenceFinder
import com.cognifide.apm.core.grammar.ScriptExecutionException
import com.cognifide.apm.core.grammar.datasource.DataSourceInvoker
import com.cognifide.apm.core.history.History
import com.cognifide.apm.core.services.version.VersionService
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.slf4j.LoggerFactory
import java.util.function.Predicate

@Component(
        service = [ModifiedScriptFinder::class],
        property = [
            Property.VENDOR
        ])
class ModifiedScriptFinder {

    private val logger = LoggerFactory.getLogger(ModifiedScriptFinder::class.java)

    @Reference
    @Transient
    lateinit var versionService: VersionService

    @Reference
    @Transient
    lateinit var scriptFinder: ScriptFinder

    @Reference
    @Transient
    lateinit var history: History

    @Reference
    @Transient
    lateinit var dataSourceInvoker: DataSourceInvoker

    fun findAll(filter: Predicate<Script>, resolver: ResourceResolver): List<Script> {
        val all = scriptFinder.findAll(filter, resolver)
        val referenceFinder = ReferenceFinder(scriptFinder, resolver, dataSourceInvoker)
        val modified = mutableListOf<Script>()

        all
                .filter { it.isValid }
                .forEach { script ->
                    try {
                        val subtree = referenceFinder.findReferences(script)
                        val checksum = versionService.countChecksum(subtree)
                        val scriptVersion = versionService.getScriptVersion(resolver, script)
                        var scriptHistory = history.findScriptHistory(resolver, script)
                        if (checksum != scriptVersion.lastChecksum
                                || scriptHistory.lastLocalRun == null
                                || checksum != scriptHistory.lastLocalRun.checksum) {
                            modified.add(script)
                        }
                    } catch (e: ScriptExecutionException) {
                        logger.error(e.message)
                    }
                }
        return modified
    }
}