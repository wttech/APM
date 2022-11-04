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

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.api.status.Status
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.*
import com.cognifide.apm.core.grammar.argument.ArgumentResolverException
import com.cognifide.apm.core.grammar.argument.Arguments
import com.cognifide.apm.core.grammar.argument.toPlainString
import com.cognifide.apm.core.grammar.common.getIdentifier
import com.cognifide.apm.core.grammar.executioncontext.ExecutionContext
import com.cognifide.apm.core.grammar.parsedscript.InvalidSyntaxException
import com.cognifide.apm.core.grammar.parsedscript.InvalidSyntaxMessageFactory
import com.cognifide.apm.core.grammar.utils.ImportScript
import com.cognifide.apm.core.grammar.utils.RequiredVariablesChecker
import com.cognifide.apm.core.logger.Position
import com.cognifide.apm.core.logger.Progress
import org.antlr.v4.runtime.ParserRuleContext
import org.apache.sling.api.resource.ResourceResolver

class ScriptRunner(
    private val scriptFinder: ScriptFinder,
    private val resourceResolver: ResourceResolver,
    private val validateOnly: Boolean = false,
    private val actionInvoker: ActionInvoker
) {

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

    private inner class Executor(private val executionContext: ExecutionContext) :
        com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor<Unit>() {

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
            val values: List<Map<String, ApmType>> = readValues(ctx)
            for ((index, value) in values.withIndex()) {
                try {
                    executionContext.createLocalContext()
                    val valueStr = value.map { it.key + "=" + it.value }
                        .joinToString()
                    progress(ctx, Status.SUCCESS, "for-each", "$index. Begin: $valueStr")
                    value.forEach { (k, v) ->
                        if (v !is ApmMap) {
                            executionContext.setVariable(k, v)
                        } else {
                            v.map.forEach { (k1, v1) -> executionContext.setVariable("$k.$k1", v1) }
                        }
                    }
                    visit(ctx.body())
                    progress(ctx, Status.SUCCESS, "for-each", "$index. End")
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
                    progress(ctx, Status.SUCCESS, "run", "Begin: path=${loadScript.path}", arguments)
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
                val status = actionInvoker.runAction(executionContext, commandName, arguments)
                if (ctx.body() != null) {
                    if (status in listOf(Status.SUCCESS, Status.WARNING)) {
                        visit(ctx.body())
                    } else {
                        progress(
                            ctx,
                            Status.SKIPPED,
                            "code-block",
                            "Skipped due to the status of previous action: $commandName"
                        )
                    }
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

        private fun readValues(ctx: ForEachContext): List<Map<String, ApmType>> {
            val keys = listOf(ctx.IDENTIFIER().toString())
            val values = when (val variableValue = executionContext.resolveArgument(ctx.argument())) {
                is ApmList -> variableValue.list.map { listOf(it) }
                is ApmEmpty -> listOf(listOf())
                else -> listOf(listOf(variableValue))
            }
            return values.map { keys.zip(it).toMap() }
        }

        private fun progress(
            ctx: ParserRuleContext,
            status: Status = Status.SUCCESS,
            command: String,
            details: String = "",
            arguments: Arguments? = null
        ) {
            progress(ctx, status, command, listOf(details), arguments)
        }

        private fun progress(
            ctx: ParserRuleContext,
            status: Status = Status.SUCCESS,
            command: String,
            details: List<String> = listOf(),
            arguments: Arguments? = null
        ) {
            executionContext.progress.addEntry(status, details, command, "", arguments, Position(ctx.start.line))
        }
    }
}