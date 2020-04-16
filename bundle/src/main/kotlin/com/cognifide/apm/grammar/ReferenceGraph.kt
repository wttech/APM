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

package com.cognifide.apm.grammar

import com.cognifide.cq.cqsm.api.scripts.Script
import kotlin.streams.toList

class ReferenceGraph(
        private val references: MutableMap<Script, Node> = mutableMapOf(),
        val roots: MutableList<TreeRoot> = mutableListOf()
) {

    fun addRoot(script: Script): Node {
        val root = TreeRoot(script)
        references[script] = root
        roots.add(root)
        return root
    }

    fun getAllReferences(): List<Script> {
        return references.keys.stream().distinct().toList()
    }

    fun getNode(script: Script): Node? {
        return references[script]
    }

    fun contains(script: Script): Boolean {
        return references.containsKey(script)
    }

    open inner class Node(
            val script: Script,
            val children: MutableList<Node> = mutableListOf()
    ) {

        open fun isValid(): Boolean = true

        fun addChild(value: Script): Node {
            val child = Node(value)
            children.add(child)
            references[value] = child
            return child
        }

        fun addChild(child: Node): Node {
            children.add(child)
            return child
        }
    }

    inner class TreeRoot(value: Script) : Node(value) {
        private var _invalidDescendants: List<Node>? = null
        val invalidDescendants: List<Node>
            get() {
                if (_invalidDescendants == null) {
                    val foundInvalidDescendants = mutableListOf<Node>()
                    children.forEach { findInvalidDescendants(foundInvalidDescendants, it) }
                    _invalidDescendants = foundInvalidDescendants.toList()
                }
                return _invalidDescendants as List<Node>
            }

        override fun isValid(): Boolean {
            return invalidDescendants.isEmpty()
        }

        private fun findInvalidDescendants(foundInvalidDescendants: MutableList<Node>, node: Node) {
            if (!node.isValid()) foundInvalidDescendants.add(node)
            if (node.children.isEmpty()) return
            return node.children.forEach { findInvalidDescendants(foundInvalidDescendants, it) }
        }
    }

    inner class CyclicNode(value: Script) : Node(value) {
        override fun isValid(): Boolean = false
    }

    inner class NonExistingNode(value: Script) : Node(value) {
        override fun isValid(): Boolean = false
    }
}