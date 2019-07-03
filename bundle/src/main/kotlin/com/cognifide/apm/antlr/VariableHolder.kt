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

package com.cognifide.apm.antlr

import java.util.*

class VariableHolder {

    private val contexts = ArrayDeque<Context>()

    init {
        createLocalContext()
    }

    operator fun set(name: String, value: ApmType) {
        contexts.peek()[name] = value
    }

    operator fun get(name: String): ApmType? {
        for (context in contexts) {
            if (context.containsKey(name)) {
                return context[name]
            }
            if (context.isIsolated) {
                break
            }
        }
        return null
    }

    fun createIsolatedLocalContext() {
        contexts.push(Context(true))
    }

    fun createLocalContext() {
        contexts.push(Context(false))
    }

    fun removeLocalContext() {
        contexts.pop()
    }

    private class Context(val isIsolated: Boolean = false) {

        private val variables = HashMap<String, ApmType>()

        fun containsKey(key: Any): Boolean {
            return variables.containsKey(key)
        }

        operator fun set(key: String, value: ApmType) {
            variables[key] = value
        }

        operator fun get(key: Any): ApmType? {
            return variables[key]
        }
    }
}