package com.cognifide.apm.core.endpoints

import com.cognifide.apm.core.grammar.ReferenceGraph

class NodeDto(treeNode: ReferenceGraph.TreeNode) {
    val script: ScriptDto = ScriptDto(treeNode.script)
    val valid: Boolean = treeNode.valid
}