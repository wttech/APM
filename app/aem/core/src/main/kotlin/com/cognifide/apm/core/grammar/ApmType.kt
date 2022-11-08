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

package com.cognifide.apm.core.grammar

import com.cognifide.apm.core.crypto.DecryptionService

abstract class ApmType {
    abstract fun getArgument(decryptionService: DecryptionService): Any?
    open val integer: Int?
        get() = null
    open val string: String?
        get() = null
    open val list: List<ApmType>?
        get() = null
    open val map: Map<String, ApmType>?
        get() = null
    open val pair: Pair<String, ApmType>?
        get() = null
}

data class ApmInteger(val value: Int) : ApmType() {
    override fun getArgument(decryptionService: DecryptionService): Int = value

    override val integer: Int
        get() = value

    override val string: String
        get() = value.toString()

    override fun toString(): String {
        return value.toString()
    }
}

data class ApmString(val value: String) : ApmType() {
    override fun getArgument(decryptionService: DecryptionService) = decryptionService.decrypt(value)

    override val string: String
        get() = value

    override fun toString(): String {
        return "\"$value\""
    }
}

data class ApmList(val value: List<ApmType>) : ApmType() {
    override fun getArgument(decryptionService: DecryptionService) = value.map { it.getArgument(decryptionService) }

    override val list: List<ApmType>
        get() = value

    override fun toString(): String {
        return value.joinToString(prefix = "[", postfix = "]") { "$it" }
    }
}

data class ApmMap(val value: Map<String, ApmType>) : ApmType() {
    override fun getArgument(decryptionService: DecryptionService) =
        value.mapValues { it.value.getArgument(decryptionService) }

    override val map: Map<String, ApmType>
        get() = value

    override fun toString(): String {
        return value.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}:${it.value}" }
    }
}

data class ApmPair(val value: Pair<String, ApmType>) : ApmType() {
    override fun getArgument(decryptionService: DecryptionService) =
        Pair(value.first, value.second.getArgument(decryptionService))

    override val pair: Pair<String, ApmType>
        get() = value

    override fun toString(): String {
        return "${value.first}:${value.second}"
    }
}

class ApmEmpty : ApmType() {
    override fun getArgument(decryptionService: DecryptionService): Any? = null
}
