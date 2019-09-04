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