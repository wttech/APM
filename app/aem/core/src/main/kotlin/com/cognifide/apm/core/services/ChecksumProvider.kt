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

package com.cognifide.apm.core.services

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.grammar.ReferenceFinder
import com.cognifide.apm.core.grammar.ReferenceGraph
import com.cognifide.cq.cqsm.api.scripts.ModifiableScript
import com.cognifide.cq.cqsm.core.scripts.ModifiableScriptWrapper
import org.apache.commons.codec.digest.DigestUtils
import org.apache.sling.api.resource.ResourceResolver

fun applyChecksum(scriptFinder: ScriptFinder, resolver: ResourceResolver, vararg scripts: Script) {
    val referenceGraph = ReferenceFinder(scriptFinder, resolver).getReferenceGraph(*scripts)

    scripts.forEach { script ->
        val checksum = countChecksum(referenceGraph.getRoot(script)!!)
        if (checksum != script.checksum) {
            val modifiableScript: ModifiableScript = ModifiableScriptWrapper(resolver, script)
            modifiableScript.setChecksum(checksum)
        }
    }
}

fun countChecksum(root: ReferenceGraph.TreeRoot): String {
    val checksums = root
            .asSequence()
            .filter { it == root || it.isValid() }
            .map { it.script.data }
            .map { DigestUtils.md5Hex(it) }
            .reduce { previous, current -> previous + current }
    return DigestUtils.md5Hex(checksums)
}