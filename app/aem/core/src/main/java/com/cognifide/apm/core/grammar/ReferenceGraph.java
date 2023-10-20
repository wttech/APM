/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.grammar

import com.cognifide.apm.api.scripts.Script
import java.util.*

class ReferenceGraph(
    val nodes: MutableList<TreeNode> = mutableListOf(),
    val transitions: MutableList<Transition> = mutableListOf()
) {

    fun addNode(script: Script): TreeNode {
        val node = TreeNodeFactory().create(script)
        nodes.add(node)
        return node
    }

    fun getNode(script: Script): TreeNode? {
        return nodes.find { it.getScriptPath() == script.path }
    }

    fun createTransition(fromNode: TreeNode, toScript: Script, transitionType: TransitionType) {
        val toNode = getNode(toScript) ?: addNode(toScript)
        transitions.add(Transition(fromNode, toNode, transitionType))
    }

    fun getSubTreeForScript(script: Script): Set<TreeNode> {
        val currentNode = getNode(script)
        val subtree = mutableSetOf<TreeNode>()
        if (currentNode != null) {
            subtree.add(currentNode)
            transitions.filter { it.from === currentNode }.forEach {
                subtree.addAll(getSubTreeForScript(it.to.script))
            }
        }
        return subtree
    }

    fun getCycleIfExist(): Transition? {
        return transitions.firstOrNull { it.cycleDetected }
    }

    fun detectCycle(fromNode: TreeNode, to: Script) {
        transitions.first { it.from == fromNode && it.to == getNode(to) }.cycleDetected = true
    }

    inner class Transition(val from: TreeNode, val to: TreeNode, val transitionType: TransitionType) {
        var cycleDetected: Boolean = false

        var id: String = "${from.id}|${to.id}|${transitionType.name}"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Transition

            return other.id == this.id
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }
    }

    enum class TransitionType {
        IMPORT, RUN_SCRIPT
    }

    open class TreeNode(val script: Script) {
        var visited = false
        var id: String = Base64.getEncoder().encodeToString(script.path.toByteArray())
        open var valid = true

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TreeNode

            return script.path == other.script.path
        }

        override fun hashCode(): Int {
            return script.hashCode()
        }

        fun getScriptPath(): String {
            return script.path
        }
    }

    class NonExistingTreeNode(script: Script) : TreeNode(script) {
        override var valid = false
    }
}

class TreeNodeFactory {

    fun create(script: Script): ReferenceGraph.TreeNode {
        if (script is ReferenceFinder.NonExistingScript) {
            return ReferenceGraph.NonExistingTreeNode(script)
        }
        return ReferenceGraph.TreeNode(script)
    }
}