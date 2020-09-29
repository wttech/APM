package com.cognifide.apm.core.grammar

import com.cognifide.apm.api.scripts.Script

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
    }

    enum class TransitionType {
        IMPORT, RUN_SCRIPT
    }

    open class TreeNode(
            val script: Script
    ) {
        var visited = false
        open var valid = true

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TreeNode

            if (script.path != other.script.path) return false

            return true
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