package com.cognifide.apm.core.endpoints

import com.cognifide.apm.core.grammar.ReferenceGraph

class RootDto(treeRoot: ReferenceGraph.TreeRoot) {
    val script: ScriptDto = ScriptDto(treeRoot.script)
    val children:  List<ScriptDto> = treeRoot.children.map { ScriptDto(it.script) }
}