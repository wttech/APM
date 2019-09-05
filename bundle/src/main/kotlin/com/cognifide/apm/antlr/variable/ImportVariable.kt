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

package com.cognifide.apm.antlr.variable

import com.cognifide.apm.antlr.ApmLangBaseVisitor
import com.cognifide.apm.antlr.ApmLangParser
import com.cognifide.apm.antlr.ApmType
import com.cognifide.apm.antlr.argument.toPlainString
import com.cognifide.apm.antlr.executioncontext.ExecutionContext
import com.cognifide.apm.antlr.parsedscript.ParsedScript
import com.google.common.base.Joiner

class ImportVariable(private val executionContext: ExecutionContext) {

    fun importAllVariables(ctx: ApmLangParser.ImportScriptContext, ns: String? = null): Result {
        val path = ctx.path().STRING_LITERAL().toPlainString()
        val namespace = calculateNamespace(ns, ctx)
        val loadScript = executionContext.loadScript(path)

        val varFromScript = findVarInScript(loadScript, namespace)
        val varFromImportedScript = findVarInImportedScripts(loadScript, namespace)

        return Result(path, namespace,(varFromImportedScript + varFromScript).toMap())
    }

    private fun calculateNamespace(ns: String?, ctx: ApmLangParser.ImportScriptContext): String? =
         when (val nsFromDefinition = ctx.`as`()?.name()?.IDENTIFIER()?.toString()) {
            null -> ns
            else -> joinName(ns, nsFromDefinition)
        }


    private fun findVarInScript(script: ParsedScript, nameSpace: String?): Map<String, ApmType> =
         VariableDefinitionsFinder().find(script)
                .map { (name, value) -> joinName(nameSpace, name) to value }
                .toMap()

    private fun findVarInImportedScripts(script: ParsedScript, ns: String?): Map<String, ApmType> {
        val ivv = ImportVariableVisitor(ns, executionContext)
        ivv.visit(script.apm)

        return ivv.variables.toMap()
    }

    private fun joinName(nameSpace: String?, name: String?): String {
        return Joiner.on("_")
                .skipNulls()
                .join(nameSpace, name)
    }

    private inner class ImportVariableVisitor(val namespace: String?, val executionContext: ExecutionContext) : ApmLangBaseVisitor<Unit>() {
        val variables = mutableMapOf<String, ApmType>()

        override fun visitImportScript(ctx: ApmLangParser.ImportScriptContext) {
            val iv = ImportVariable(executionContext)

            variables.putAll(iv.importAllVariables(ctx, namespace).variables)
        }
    }

     inner class Result(val path: String, val ns: String?, val variables: Map<String, ApmType>)
}