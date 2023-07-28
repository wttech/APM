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

package com.cognifide.apm.core.grammar.utils

import com.cognifide.apm.core.grammar.argument.Arguments
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript

class RequiredVariablesChecker {

    fun checkNamedArguments(parsedScript: ParsedScript, arguments: Arguments): Result {
        val requireVariableVisitor = RequireVariableVisitor()
        requireVariableVisitor.visit(parsedScript.apm)
        val missingNamedArguments = requireVariableVisitor.variables.filter { !arguments.named.containsKey(it) }
        return Result(missingNamedArguments)
    }

    private inner class RequireVariableVisitor : com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor<Unit>() {

        val variables = mutableListOf<String>()

        override fun visitRequireVariable(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.RequireVariableContext) {
            variables.add(ctx.IDENTIFIER().toString())
        }
    }

    class Result(private val missingNamedArguments: List<String>) {
        val isValid: Boolean get() = missingNamedArguments.isEmpty()
        fun toMessages(): List<String> = missingNamedArguments.map { "Parameter \"$it\" is required" }
    }
}