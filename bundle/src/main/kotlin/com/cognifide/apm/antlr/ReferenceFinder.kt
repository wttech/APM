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

package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.argument.toPlainString
import com.cognifide.apm.antlr.executioncontext.ExecutionContext
import com.cognifide.apm.antlr.parsedscript.ParsedScript
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import com.cognifide.cq.cqsm.core.progress.ProgressImpl
import org.apache.sling.api.resource.ResourceResolver

class ReferenceFinder(
        private val scriptFinder: ScriptFinder,
        private val resourceResolver: ResourceResolver) {

    fun findReferences(script: Script): List<Script> {
        val references = mutableListOf<Script>()
        val parsedScript = ParsedScript.create(script).apm
        val executionContext = ExecutionContext.create(scriptFinder, resourceResolver, script, ProgressImpl(resourceResolver.userID))
        findReferences(references, parsedScript, executionContext)
        return references.toList()
    }

    private fun findReferences(references: MutableList<Script>, ctx: ApmLangParser.ApmContext, executionContext: ExecutionContext): MutableSet<Script> {
        val internalVisitor = InternalVisitor(executionContext)
        internalVisitor.visitApm(ctx)

        internalVisitor.scripts.forEach {
            if (!references.contains(it)) {
                references.add(it)
                val parsedScript = executionContext.loadScript(it.path)
                findReferences(references, parsedScript.apm, executionContext)
            }
        }
        return internalVisitor.scripts
    }

    inner class InternalVisitor(private val executionContext: ExecutionContext) : ApmLangBaseVisitor<Unit>() {
        val scripts = mutableSetOf<Script>()

        override fun visitImportScript(ctx: ApmLangParser.ImportScriptContext?) {
            val foundPath = ctx?.path()?.STRING_LITERAL()?.toPlainString()
            if (foundPath != null) {
                val script = executionContext.loadScript(foundPath).script
                scripts.add(script)
            }
        }

        override fun visitRunScript(ctx: ApmLangParser.RunScriptContext?) {
            val foundPath = ctx?.path()?.STRING_LITERAL()?.toPlainString()
            if (foundPath != null) {
                val script = executionContext.loadScript(foundPath).script
                scripts.add(script)
            }
        }
    }
}