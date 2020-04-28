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

package com.cognifide.apm.core.grammar

import com.cognifide.apm.api.scripts.LaunchEnvironment
import com.cognifide.apm.api.scripts.LaunchMode
import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.grammar.ReferenceGraph.CyclicNode
import com.cognifide.apm.core.grammar.ReferenceGraph.NonExistingNode
import com.cognifide.apm.core.grammar.argument.toPlainString
import com.cognifide.apm.core.grammar.executioncontext.ExecutionContext
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript
import com.cognifide.cq.cqsm.core.progress.ProgressImpl
import org.apache.sling.api.resource.ResourceResolver
import java.util.*

class ReferenceFinder(
        private val scriptFinder: ScriptFinder,
        private val resourceResolver: ResourceResolver) {

    fun findReferences(script: Script): List<Script> {
        val referenceGraph = ReferenceGraph()
        fillReferenceGraph(referenceGraph, script)
        val treeRoot = referenceGraph.roots.first()
        if (treeRoot.isValid()) {
            return referenceGraph.getAllReferences().remove(script)
        } else {
            val message = when (val invalidDescendant = treeRoot.invalidDescendants.first()) {
                is NonExistingNode -> "Script doesn't exist: ${invalidDescendant.script.path}"
                is CyclicNode -> "Found cycle: ${invalidDescendant.script.path}"
                else -> "Invalid reference to: ${invalidDescendant.script.path}"
            }
            throw ScriptExecutionException(message)
        }
    }

    fun getReferenceGraph(vararg scripts: Script): ReferenceGraph {
        val referenceGraph = ReferenceGraph()
        scripts.forEach {
            fillReferenceGraph(referenceGraph, it)
        }
        return referenceGraph
    }

    private fun fillReferenceGraph(referenceGraph: ReferenceGraph, script: Script): ReferenceGraph {
        val root = referenceGraph.addRoot(script)
        val parsedScript = ParsedScript.create(script).apm
        val executionContext = ExecutionContext.create(scriptFinder, resourceResolver, script, ProgressImpl(resourceResolver.userID))
        findReferences(referenceGraph, root, listOf(script), parsedScript, executionContext)
        return referenceGraph
    }

    fun <T> List<T>.add(value: T): List<T> {
        val mutable = this.toMutableList()
        mutable.add(value)
        return mutable.toList()
    }

    fun <T> List<T>.remove(value: T): List<T> {
        val mutable = this.toMutableList()
        mutable.remove(value)
        return mutable.toList()
    }

    private fun findReferences(
            referenceGraph: ReferenceGraph, parent: ReferenceGraph.Node, ancestors: List<Script>,
            ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.ApmContext, executionContext: ExecutionContext): MutableSet<Script> {

        val internalVisitor = InternalVisitor(executionContext)
        internalVisitor.visitApm(ctx)

        internalVisitor.scripts.forEach { script ->
            when {
                script is NonExistingScript -> parent.addChild(referenceGraph.NonExistingNode(script))
                ancestors.contains(script) -> parent.addChild(referenceGraph.CyclicNode(script))
                referenceGraph.contains(script) -> parent.addChild(referenceGraph.getNode(script)!!)
                else -> {
                    val node = parent.addChild(script)
                    val parsedScript = executionContext.loadScript(script.path)
                    executionContext.createScriptContext(parsedScript)
                    findReferences(referenceGraph, node, ancestors.add(script), parsedScript.apm, executionContext)
                    executionContext.removeScriptContext()
                }
            }
        }
        return internalVisitor.scripts
    }

    inner class InternalVisitor(private val executionContext: ExecutionContext) : com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor<Unit>() {
        val scripts = mutableSetOf<Script>()

        override fun visitImportScript(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.ImportScriptContext?) {
            val foundPath = ctx?.path()?.STRING_LITERAL()?.toPlainString()
            loadScript(foundPath)
        }

        override fun visitRunScript(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.RunScriptContext?) {
            val foundPath = ctx?.path()?.STRING_LITERAL()?.toPlainString()
            loadScript(foundPath)
        }

        private fun loadScript(foundPath: String?) {
            if (foundPath != null) {
                try {
                    val script = executionContext.loadScript(foundPath).script
                    scripts.add(script)
                } catch (e: ScriptExecutionException) {
                    scripts.add(NonExistingScript(foundPath))
                }
            }
        }
    }

    class NonExistingScript(val scriptPath: String) : Script {
        override fun refresh() {}

        override fun getPath(): String = scriptPath

        override fun isValid(): Boolean = false

        override fun isLaunchEnabled(): Boolean = false

        override fun getLaunchMode(): LaunchMode = LaunchMode.ON_DEMAND

        override fun getLaunchEnvironment(): LaunchEnvironment? = null

        override fun getLaunchHook(): String? = null

        override fun getLaunchSchedule(): Date? = null

        override fun getLastExecution(): Date? = null

        override fun isPublishRun(): Boolean = false

        override fun getChecksum(): String? = null

        override fun getAuthor(): String? = null

        override fun getLastModified(): Date? = null

        override fun getData(): String? = null

        override fun getReplicatedBy(): String? = null
    }

}
