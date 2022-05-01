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

package com.cognifide.apm.core.grammar

import com.cognifide.apm.api.scripts.LaunchEnvironment
import com.cognifide.apm.api.scripts.LaunchMode
import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.grammar.executioncontext.ExecutionContext
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript
import com.cognifide.apm.core.progress.ProgressImpl
import org.apache.sling.api.resource.ResourceResolver
import java.util.*

class ReferenceFinder(
        private val scriptFinder: ScriptFinder,
        private val resourceResolver: ResourceResolver) {

    fun findReferences(script: Script): List<Script> {
        val result = mutableSetOf<Script>()
        val refGraph = getReferenceGraph(script)
        val possibleCycle = refGraph.getCycleIfExist()
        if (possibleCycle != null) {
            throw ScriptExecutionException("Cycle detected ${possibleCycle.from.getScriptPath()} -> ${possibleCycle.to.getScriptPath()}")
        } else {
            refGraph.getSubTreeForScript(script).forEach {
                if (it is ReferenceGraph.NonExistingTreeNode) {
                    throw ScriptExecutionException("Script doesn't exist ${it.getScriptPath()}")
                } else {
                    result.add(it.script)
                }
            }
        }
        return result.toList()
    }

    fun getReferenceGraph(vararg scripts: Script): ReferenceGraph {
        val refGraph = ReferenceGraph()
        scripts.forEach {
            fillReferenceGraph(refGraph, it)
        }
        return refGraph
    }

    private fun fillReferenceGraph(refGraph: ReferenceGraph, script: Script) {
        if (refGraph.getNode(script) == null) {
            val parsedScript = ParsedScript.create(script).apm
            val executionContext = ExecutionContext.create(scriptFinder, resourceResolver, script, ProgressImpl(resourceResolver.userID))
            findReferences(refGraph, refGraph.addNode(script), listOf(script), executionContext, parsedScript)
        }
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

    private fun findReferences(refGraph: ReferenceGraph, currentNode: ReferenceGraph.TreeNode, ancestors: List<Script>,
                               executionContext: ExecutionContext,
                               ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.ApmContext) {
        val internalVisitor = InternalVisitor(executionContext, refGraph, currentNode)
        internalVisitor.visitApm(ctx)
        currentNode.visited = true
        internalVisitor.scripts.forEach { script ->
            val node = refGraph.getNode(script)
            if (node != null && node !is ReferenceGraph.NonExistingTreeNode) {
                val parsedScript = executionContext.loadScript(script.path)
                executionContext.createScriptContext(parsedScript)
                if (ancestors.contains(script)) {
                    refGraph.detectCycle(currentNode, script)
                } else if (!node.visited) {
                    findReferences(refGraph, node, ancestors.add(script), executionContext, parsedScript.apm)
                }
            }
        }
    }

    inner class InternalVisitor(private val executionContext: ExecutionContext, val refGraph: ReferenceGraph, val currentNode: ReferenceGraph.TreeNode) : com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor<Unit>() {
        val scripts = mutableSetOf<Script>()

        override fun visitImportScript(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.ImportScriptContext?) {
            val foundPath = ctx?.path()?.STRING_LITERAL()?.toPlainString()
            createTransition(foundPath, ReferenceGraph.TransitionType.IMPORT)
        }

        override fun visitRunScript(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.RunScriptContext?) {
            val foundPath = ctx?.path()?.STRING_LITERAL()?.toPlainString()
            createTransition(foundPath, ReferenceGraph.TransitionType.RUN_SCRIPT)
        }

        private fun createTransition(foundPath: String?, transitionType: ReferenceGraph.TransitionType) {
            val script: Script? = loadScript(foundPath)
            if (script != null) {
                refGraph.createTransition(currentNode, script, transitionType)
                scripts.add(script)
            }
        }

        private fun loadScript(foundPath: String?): Script? {
            var script: Script? = null
            if (foundPath != null) {
                try {
                    script = executionContext.loadScript(foundPath).script
                } catch (e: ScriptExecutionException) {
                    script = NonExistingScript(foundPath)
                    currentNode.valid = false
                }
            }
            return script
        }
    }

    class NonExistingScript(val scriptPath: String) : Script {

        override fun getPath(): String = scriptPath

        override fun isValid(): Boolean = false

        override fun isLaunchEnabled(): Boolean = false

        override fun getLaunchMode(): LaunchMode = LaunchMode.ON_DEMAND

        override fun getLaunchEnvironment(): LaunchEnvironment? = null

        override fun getLaunchRunModes(): Set<String>? = null

        override fun getLaunchHook(): String? = null

        override fun getLaunchSchedule(): Date? = null

        override fun getLastExecuted(): Date? = null

        override fun getChecksum(): String? = null

        override fun getAuthor(): String? = null

        override fun getLastModified(): Date? = null

        override fun getData(): String? = null
    }

}
