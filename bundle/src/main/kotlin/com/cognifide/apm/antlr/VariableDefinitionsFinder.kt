package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.argument.ArgumentResolver
import com.cognifide.apm.antlr.executioncontext.VariableHolder
import com.cognifide.apm.antlr.parsedscript.ParsedScript

class VariableDefinitionsFinder  {

    fun find(parsedScript: ParsedScript) : Map<String, ApmType>{
        val finder = DefinitionFinder()
        finder.visit(parsedScript.apm)
        return finder.variables
    }

    private inner class DefinitionFinder() : ApmLangBaseVisitor<Unit>(){
        val variables = mutableMapOf<String, ApmType>()
        val variableHolder = VariableHolder()
        val argumentResolver = ArgumentResolver(variableHolder)


        override fun visitDefineVariable(ctx: ApmLangParser.DefineVariableContext) {
            val value = argumentResolver.resolve(ctx.argument())
            this.variables.put(ctx.IDENTIFIER().toString(), value)
        }
    }
}