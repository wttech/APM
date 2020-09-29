package com.cognifide.apm.core.endpoints

import com.cognifide.apm.core.grammar.ReferenceGraph

class TransitionDto(transition: ReferenceGraph.Transition) {
    val cycleDetected = transition.cycleDetected
    val from: NodeDto = NodeDto(transition.from)
    val to: NodeDto = NodeDto(transition.to)
    val refType: String = transition.transitionType.name
}