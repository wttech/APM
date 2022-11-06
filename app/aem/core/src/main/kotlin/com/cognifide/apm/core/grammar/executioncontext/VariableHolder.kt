/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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

package com.cognifide.apm.core.grammar.executioncontext

import com.cognifide.apm.core.grammar.ApmList
import com.cognifide.apm.core.grammar.ApmMap
import com.cognifide.apm.core.grammar.ApmType
import com.cognifide.apm.core.grammar.common.StackWithRoot
import org.apache.jackrabbit.api.security.user.Authorizable

class VariableHolder {

    private val contexts = StackWithRoot(Context())

    var authorizable: Authorizable?
        get() {
            for (context in contexts) {
                if (context.authorizable != null) {
                    return context.authorizable
                }
            }
            return null
        }
        set(authorizable) {
            contexts.peek().authorizable = authorizable
        }

    init {
        createLocalContext()
    }

    operator fun set(name: String, value: ApmType) {
        contexts.peek()[name] = value
    }

    fun setAll(variableHolder: VariableHolder) {
        contexts.peek().variables.putAll(variableHolder.contexts.peek().variables)
    }

    operator fun get(name: String): ApmType? {
        val keys = name.split('.', '[', ']')
        val context = contexts.firstOrNull { it.containsKey(keys[0]) }
        var result: ApmType? = null
        if (context != null) {
            for (key in keys) {
                if (key.isEmpty()) {
                    continue
                }
                result = when (result) {
                    is ApmList -> result.list.getOrNull(key.toInt())
                    is ApmMap -> result.map.get(key)
                    else -> context.get(key)
                }
                if (result == null) {
                    break
                }
            }
        }
        return result
    }

    fun createLocalContext() {
        contexts.push(Context())
    }

    fun removeLocalContext() {
        contexts.pop()
    }

    fun toMap(): Map<String, ApmType> {
        return contexts.peek().toMap()
    }

    private class Context(var authorizable: Authorizable? = null) {

        val variables = mutableMapOf<String, ApmType>()

        fun containsKey(key: Any): Boolean {
            return variables.containsKey(key)
        }

        operator fun set(key: String, value: ApmType) {
            variables[key] = value
        }

        operator fun get(key: Any): ApmType? {
            return variables[key]
        }

        fun toMap(): Map<String, ApmType> {
            return variables.toMap()
        }
    }
}