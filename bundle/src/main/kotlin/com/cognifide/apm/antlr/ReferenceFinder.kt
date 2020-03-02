package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.argument.toPlainString
import com.cognifide.apm.antlr.executioncontext.ExecutionContext
import com.cognifide.cq.cqsm.api.scripts.Script

class ReferenceFinder(private val executionContext: ExecutionContext) {
    fun findReferences(ctx: ApmLangParser.ApmContext): List<Script> {
        val references = mutableListOf<Script>()
        findReferences(references, ctx)
        return references.toList()
    }

    private fun findReferences(references: MutableList<Script>, ctx: ApmLangParser.ApmContext): MutableSet<Script> {
        val internalVisitor = InternalVisitor()
        internalVisitor.visitApm(ctx)

        internalVisitor.scripts.forEach {
            if (!references.contains(it)) {
                references.add(it)
                val parsedScript = executionContext.loadScript(it.path)
                findReferences(references, parsedScript.apm)
            }
        }
        return internalVisitor.scripts
    }

    inner class InternalVisitor : ApmLangBaseVisitor<Unit>() {
        val scripts = mutableSetOf<Script>()

        override fun visitImportScript(ctx: ApmLangParser.ImportScriptContext?) {
            val foundPath = ctx?.path()?.STRING_LITERAL()?.toPlainString()
            if (foundPath != null) {
                val script = executionContext.loadScript(foundPath).script
                scripts.add(script)
            }
        }

        override fun visitRunScript(ctx: ApmLangParser.RunScriptContext?) {
            val foundPath = ctx?.path()?.STRING_LITERAL()?.toPlainString()
            if (foundPath != null) {
                val script = executionContext.loadScript(foundPath).script
                scripts.add(script)
            }
        }
    }
}