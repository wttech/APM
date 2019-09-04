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
import com.google.common.base.Joiner

class ImportVariable(private val executionContext: ExecutionContext)  {

    fun importAllVariables(scriptPath: String, namespace: String?): Map<String, ApmType>{
        val loadScript = executionContext.loadScript(scriptPath)
        val ivv = ImportVariableVisitor(namespace, executionContext)
        ivv.visit(loadScript.apm)
        val varFinder = VariableDefinitionsFinder()
        ivv.variables.putAll(varFinder.find(loadScript)
                .map { (name, value) -> joinName(namespace, name) to value }
                .toMap())

        return ivv.variables
    }

    private fun joinName(nameSpace: String?, name: String?): String {
        return Joiner.on("_")
                .skipNulls()
                .join(nameSpace, name)
    }

    private inner class ImportVariableVisitor(val namespace: String?, val executionContext: ExecutionContext) : ApmLangBaseVisitor<Unit>() {
        val variables = mutableMapOf<String, ApmType>()

        override fun visitImportScript(ctx: ApmLangParser.ImportScriptContext) {
            val path = ctx.path().STRING_LITERAL().toPlainString()
            val ns = ctx.`as`()?.name()?.IDENTIFIER()?.toString()
            val iv = ImportVariable(executionContext)

            variables.putAll(iv.importAllVariables(path, joinName(ns, namespace)))
        }
    }
}