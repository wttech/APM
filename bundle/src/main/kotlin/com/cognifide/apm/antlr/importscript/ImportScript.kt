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

package com.cognifide.apm.antlr.importscript

import com.cognifide.apm.antlr.ApmLangBaseVisitor
import com.cognifide.apm.antlr.ApmLangParser
import com.cognifide.apm.antlr.argument.ArgumentResolver
import com.cognifide.apm.antlr.argument.toPlainString
import com.cognifide.apm.antlr.executioncontext.ExecutionContext
import com.cognifide.apm.antlr.executioncontext.VariableHolder

class ImportScript(private val executionContext: ExecutionContext) {

    fun import(ctx: ApmLangParser.ImportScriptContext): Result {
        val path = getPath(ctx)
        val importScriptVisitor = ImportScriptVisitor()
        importScriptVisitor.visit(ctx)
        return Result(path, importScriptVisitor.variableHolder)
    }

    private fun getNamespace(ctx: ApmLangParser.ImportScriptContext): String =
            if (ctx.name() != null) {
                ctx.name().IDENTIFIER().toPlainString() + "_"
            } else {
                ""
            }

    private fun getPath(ctx: ApmLangParser.ImportScriptContext) =
            ctx.path().STRING_LITERAL().toPlainString()

    private inner class ImportScriptVisitor() : ApmLangBaseVisitor<Unit>() {
        val variableHolder = VariableHolder()

        val argumentResolver = ArgumentResolver(variableHolder)

        override fun visitDefineVariable(ctx: ApmLangParser.DefineVariableContext) {
            val variableName = ctx.IDENTIFIER().toString()
            val variableValue = argumentResolver.resolve(ctx.argument())
            variableHolder[variableName] = variableValue
        }

        override fun visitImportScript(ctx: ApmLangParser.ImportScriptContext) {
            val path = getPath(ctx)
            val namespace = getNamespace(ctx)
            val importScriptVisitor = ImportScriptVisitor()
            val parsedScript = executionContext.loadScript(path)
            importScriptVisitor.visit(parsedScript.apm)
            val scriptVariableHolder = importScriptVisitor.variableHolder
            scriptVariableHolder.toMap().forEach { (name, value) -> variableHolder[namespace + name] = value }
        }
    }

    class Result(val path: String, val variableHolder: VariableHolder) {
        fun toMessages(): List<String> {
            val importedVariables = variableHolder.toMap().map { "Imported variable: ${it.key}= ${it.value}" }
            return listOf("Import from script $path. Notice, only DEFINE actions were processed!") + importedVariables
        }
    }
}