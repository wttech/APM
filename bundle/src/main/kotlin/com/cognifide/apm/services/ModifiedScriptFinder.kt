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

package com.cognifide.apm.services

import com.cognifide.apm.grammar.ReferenceFinder
import com.cognifide.cq.cqsm.api.history.History
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import com.cognifide.cq.cqsm.core.Property
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import java.util.function.Predicate

@Component(
        immediate = true,
        service = [ModifiedScriptFinder::class],
        property = [
            Property.VENDOR
        ])
class ModifiedScriptFinder {

    @Reference
    @Transient
    lateinit var history: History

    @Reference
    @Transient
    lateinit var scriptFinder: ScriptFinder

    fun findAll(filter: Predicate<Script>, resolver: ResourceResolver): List<Script> {
        val all = scriptFinder.findAll(filter, resolver)
        val referenceFinder = ReferenceFinder(scriptFinder, resolver)
        val referenceGraph = referenceFinder.getReferenceGraph(*all.toTypedArray())
        val modified = mutableListOf<Script>()
        referenceGraph.roots
                .filter { it.isValid() }
                .forEach { root ->
                    val checksum = countChecksum(root)
                    val scriptHistory = history.findScriptHistory(resolver, root.script)
                    if (checksum != scriptHistory.lastChecksum) {
                        modified.add(root.script)
                    }
                }
        return modified
    }
}