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

package com.cognifide.apm.grammar

import com.cognifide.apm.grammar.antlr.ApmLangBaseVisitor
import com.cognifide.apm.grammar.antlr.ApmLangParser.*
import com.cognifide.apm.grammar.argument.ArgumentResolverException
import com.cognifide.apm.grammar.argument.Arguments
import com.cognifide.apm.grammar.argument.toPlainString
import com.cognifide.apm.grammar.common.getIdentifier
import com.cognifide.apm.grammar.executioncontext.ExecutionContext
import com.cognifide.apm.grammar.parsedscript.InvalidSyntaxException
import com.cognifide.apm.grammar.parsedscript.InvalidSyntaxMessageFactory
import com.cognifide.apm.grammar.utils.ImportScript
import com.cognifide.apm.grammar.utils.RequiredVariablesChecker
import com.cognifide.cq.cqsm.api.logger.Position
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.api.logger.Status
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import org.antlr.v4.runtime.ParserRuleContext
import org.apache.sling.api.resource.ResourceResolver

class ScriptRunner(
        private val scriptFinder: ScriptFinder,
        private val resourceResolver: ResourceResolver,
        private val validateOnly: Boolean = false,
        private val actionInvoker: ActionInvoker) {

    @JvmOverloads
    fun execute(script: Script, progress: Progress, initialDefinitions: Map<String, String> = mapOf()): Progress {
        try {
            val executionContext = ExecutionContext.create(scriptFinder, resourceResolver, script, progress)
            initialDefinitions.forEach { (name, value) -> executionContext.setVariable(name, ApmString(value)) }
            val executor = Executor(executionContext)
            executor.visit(executionContext.root.apm)
        } catch (e: InvalidSyntaxException) {
            val errorMessages = InvalidSyntaxMessageFactory.detailedSyntaxError(e)
            progress.addEntry(Status.ERROR, errorMessages)
        } catch (e: ArgumentResolverException) {
            progress.addEntry(Status.ERROR, e.message)
        } catch (e: ScriptExecutionException) {
            progress.addEntry(Status.ERROR, e.message)
        }
        return progress
    }

    private inner class Executor(private val executionContext: ExecutionContext) : ApmLangBaseVisitor<Unit>() {

        override fun visitDefineVariable(ctx: DefineVariableContext) {
            val variableName = ctx.IDENTIFIER().toString()
            val variableValue = executionContext.resolveArgument(ctx.argument())
            executionContext.setVariable(variableName, variableValue)
            progress(ctx, Status.SUCCESS, "define", "Defined variable: $variableName= $variableValue")
        }

        override fun visitRequireVariable(ctx: RequireVariableContext) {
            val variableName = ctx.IDENTIFIER().toString()
            if (executionContext.getVariable(variableName) == null) {
                val status = if (validateOnly) Status.WARNING else Status.ERROR
                progress(ctx, status, "require", "Variable \"$variableName\" is required")
            }
        }

        override fun visitForEach(ctx: ForEachContext) {
            val index = ctx.IDENTIFIER().toString()
            val values: List<ApmValue> = readValues(ctx.argument())
            for ((iteration, value) in values.withIndex()) {
                try {
                    executionContext.createLocalContext()
                    progress(ctx, Status.SUCCESS, "for-each", "$iteration. Begin: $index= $value")
                    executionContext.setVariable(index, value)
                    visit(ctx.body())
                    progress(ctx, Status.SUCCESS, "for-each", "$iteration. End")
                } finally {
                    executionContext.removeLocalContext()
                }
            }
        }

        override fun visitRunScript(ctx: RunScriptContext) {
            val path = ctx.path().STRING_LITERAL().toPlainString()
            val arguments = executionContext.resolveArguments(ctx.namedArguments())
            val loadScript = executionContext.loadScript(path)
            if (executionContext.scriptIsOnStack(loadScript))
                throw ScriptExecutionException("Found cyclic reference to ${loadScript.path}")
            val result = RequiredVariablesChecker().checkNamedArguments(loadScript, arguments)
            if (result.isValid) {
                executionContext.createScriptContext(loadScript)
                try {
                    arguments.named.forEach { (key, value) -> executionContext.setVariable(key, value) }
                    progress(ctx, Status.SUCCESS, "run", "Begin: path= ${loadScript.path}", arguments)
                    visit(loadScript.apm)
                    progress(ctx, Status.SUCCESS, "run", "End")
                } finally {
                    executionContext.removeScriptContext()
                }
            } else {
                progress(ctx, Status.ERROR, "run", result.toMessages(), arguments)
            }
        }

        override fun visitGenericCommand(ctx: GenericCommandContext) {
            val commandName = getIdentifier(ctx.commandName().identifier()).toUpperCase()
            if (validateOnly) {
                visitGenericCommandValidateMode(ctx, commandName)
            } else {
                visitGenericCommandRunMode(ctx, commandName)
            }
        }

        private fun visitGenericCommandRunMode(ctx: GenericCommandContext, commandName: String) {
            try {
                if (ctx.body() != null) {
                    executionContext.createLocalContext()
                }
                val arguments = executionContext.resolveArguments(ctx.complexArguments())
                actionInvoker.runAction(executionContext, commandName, arguments)
                if (ctx.body() != null) {
                    visit(ctx.body())
                }
            } catch (e: ArgumentResolverException) {
                progress(ctx, Status.ERROR, commandName, "Action failed: ${e.message}")
            } finally {
                if (ctx.body() != null) {
                    executionContext.removeLocalContext()
                }
            }
        }

        private fun visitGenericCommandValidateMode(ctx: GenericCommandContext, commandName: String) {
            try {
                if (ctx.body() != null) {
                    executionContext.createLocalContext()
                }
                try {
                    val arguments = executionContext.resolveArguments(ctx.complexArguments())
                    actionInvoker.runAction(executionContext, commandName, arguments)
                } catch (e: ArgumentResolverException) {
                    progress(ctx, Status.WARNING, commandName, "Couldn't invoke action: ${e.message}")
                }
                if (ctx.body() != null) {
                    visit(ctx.body())
                }
            } finally {
                if (ctx.body() != null) {
                    executionContext.removeLocalContext()
                }
            }
        }

        override fun visitImportScript(ctx: ImportScriptContext) {
            val result = ImportScript(executionContext).import(ctx)
            executionContext.variableHolder.setAll(result.variableHolder)
            progress(ctx, Status.SUCCESS, "import", result.toMessages())
        }

        private fun readValues(argumentContext: ArgumentContext): List<ApmValue> {
            return when (val variableValue = executionContext.resolveArgument(argumentContext)) {
                is ApmList -> variableValue.list.map { ApmString(it) }
                is ApmEmpty -> listOf()
                else -> listOf(variableValue as ApmValue)
            }
        }

        private fun progress(ctx: ParserRuleContext, status: Status = Status.SUCCESS, command: String, details: String = "", arguments: Arguments? = null) {
            progress(ctx, status, command, listOf(details), arguments)
        }

        private fun progress(ctx: ParserRuleContext, status: Status = Status.SUCCESS, command: String, details: List<String> = listOf(), arguments: Arguments? = null) {
            executionContext.progress.addEntry(status, details, command, "", arguments, Position(ctx.start.line))
        }
    }
}