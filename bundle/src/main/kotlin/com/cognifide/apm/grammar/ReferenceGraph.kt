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
        private val references: MutableMap<Script, TreeNode> = mutableMapOf(),
        val roots: MutableList<TreeNode> = mutableListOf()
) {

    fun addRoot(script: Script): TreeNode {
        val root = TreeNode(script)
        references[script] = root
        roots.add(root)
        return root
    }

    fun getAllReferences(): List<Script> {
        return references.keys.stream().distinct().toList()
    }

    fun getNode(script: Script): TreeNode? {
        return references[script]
    }

    fun contains(script: Script): Boolean {
        return references.containsKey(script)
    }

    open inner class TreeNode(
            val script: Script,
            val children: MutableList<TreeNode> = mutableListOf()
    ) {

        fun addChild(value: Script): TreeNode {
            val child = TreeNode(value)
            children.add(child)
            references[value] = child
            return child
        }

        fun addChild(child: TreeNode): TreeNode {
            children.add(child)
            return child
        }
    }

    inner class CyclicNode(value: Script) : TreeNode(value)
}