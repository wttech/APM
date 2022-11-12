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

import com.cognifide.apm.core.grammar.ApmMap
import com.cognifide.apm.core.grammar.ScriptExecutionException
import com.cognifide.apm.core.grammar.argument.ArgumentResolver
import com.cognifide.apm.core.grammar.common.getPath
import com.cognifide.apm.core.grammar.executioncontext.ExecutionContext
import com.cognifide.apm.core.grammar.executioncontext.VariableHolder
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript

class ImportScript(private val executionContext: ExecutionContext) {

    private val visitedScripts: MutableSet<ParsedScript> = mutableSetOf()

    fun import(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.ImportScriptContext): Result {
        val path = getPath(ctx.path())
        val importScriptVisitor = ImportScriptVisitor()
        importScriptVisitor.visit(ctx)
        return Result(path, importScriptVisitor.variableHolder)
    }

    private fun getNamespace(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.ImportScriptContext): String =
        if (ctx.name() != null) {
            ctx.name().IDENTIFIER().toString()
        } else {
            ""
        }

    private inner class ImportScriptVisitor : com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor<Unit>() {
        val variableHolder = VariableHolder()

        val argumentResolver = ArgumentResolver(variableHolder, executionContext)

        override fun visitDefineVariable(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.DefineVariableContext) {
            val variableName = ctx.IDENTIFIER().toString()
            val variableValue = argumentResolver.resolve(ctx.argument())
            variableHolder[variableName] = variableValue
        }

        override fun visitImportScript(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.ImportScriptContext) {
            val path = getPath(ctx.path())
            val namespace = getNamespace(ctx)
            val importScriptVisitor = ImportScriptVisitor()
            val parsedScript = executionContext.loadScript(path)

            if (parsedScript !in visitedScripts) visitedScripts.add(parsedScript)
            else throw ScriptExecutionException("Found cyclic reference to ${parsedScript.path}")

            importScriptVisitor.visit(parsedScript.apm)
            val scriptVariableHolder = importScriptVisitor.variableHolder
            if (namespace == "") {
                variableHolder.setAll(scriptVariableHolder)
            } else {
                variableHolder[namespace] = ApmMap(scriptVariableHolder.toMap())
            }
        }
    }

    class Result(val path: String, val variableHolder: VariableHolder) {
        fun toMessages(): List<String> {
            val importedVariables = variableHolder.toMap().map { "Imported variable: ${it.key}=${it.value}" }
            return listOf("Import from script $path. Notice, only DEFINE actions were processed!") + importedVariables
        }
    }
}