package com.cognifide.apm.core.endpoints

import com.cognifide.apm.core.grammar.ReferenceGraph

class RootDto(treeRoot: ReferenceGraph.Node) {
    val script: ScriptDto = ScriptDto(treeRoot.script)
    val children:  List<RootDto> = treeRoot.children.map { RootDto(it) }
}