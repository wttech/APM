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

import com.cognifide.apm.api.scripts.Script
import java.util.*

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
        return references.keys.distinct().toList()
    }

    fun getRoot(script: Script): TreeRoot? {
        return roots.find { it.script == script }
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

    inner class TreeRoot(value: Script) : Node(value), Iterable<Node> {
        val descendants: List<Node>
            get() = this.filter { it != this }
        val invalidDescendants: List<Node>
            get() = this.filter { it != this && !it.isValid() }

        override fun isValid(): Boolean {
            return invalidDescendants.isEmpty()
        }

        override fun iterator(): Iterator<Node> = TreeIterator(this)
    }

    inner class CyclicNode(value: Script) : Node(value) {
        override fun isValid(): Boolean = false
    }

    inner class NonExistingNode(value: Script) : Node(value) {
        override fun isValid(): Boolean = false
    }

    class TreeIterator(root: Node) : Iterator<Node> {

        private val queue = LinkedList<Node>()

        init {
            queue.add(root)
        }

        override fun hasNext(): Boolean = queue.isNotEmpty()

        override fun next(): Node {
            val next = queue.pop()
            queue.addAll(next.children)
            return next
        }

    }
}