package com.cognifide.apm.core.endpoints

import com.cognifide.apm.core.grammar.ReferenceGraph
import com.google.gson.annotations.SerializedName

class GraphDto(refGraph: ReferenceGraph) {
    @SerializedName("nodes")
    val nodes: List<NodeDto> = refGraph.nodes.map { NodeDto(it) }
    @SerializedName("transitions")
    val transitions: List<TransitionDto> = refGraph.transitions.map { TransitionDto(it) }
}