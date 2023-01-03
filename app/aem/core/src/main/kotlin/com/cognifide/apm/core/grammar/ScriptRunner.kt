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
import org.antlr.v4.runtime.tree.RuleNode
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

    private inner class Executor(
        private val executionContext: ExecutionContext,
        private var globalResult: Status = Status.SUCCESS
    ) : com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor<Status>() {

        private fun shouldVisitNextChild(): Boolean {
            return globalResult != Status.ERROR
        }

        override fun shouldVisitNextChild(node: RuleNode, currentResult: Status?): Boolean {
            return shouldVisitNextChild()
        }

        override fun aggregateResult(aggregate: Status?, nextResult: Status?): Status {
            globalResult = if (nextResult == Status.ERROR) Status.ERROR else globalResult
            return globalResult
        }

        override fun visitDefineVariable(ctx: DefineVariableContext): Status {
            val variableName = ctx.IDENTIFIER().toString()
            val variableValue = executionContext.resolveArgument(ctx.argument())
            executionContext.setVariable(variableName, variableValue)
            progress(ctx, Status.SUCCESS, "define", "Defined variable: $variableName= $variableValue")
            return Status.SUCCESS
        }

        override fun visitRequireVariable(ctx: RequireVariableContext): Status {
            val variableName = ctx.IDENTIFIER().toString()
            if (executionContext.getVariable(variableName) == null) {
                val status = if (validateOnly) Status.WARNING else Status.ERROR
                progress(ctx, status, "require", "Variable \"$variableName\" is required")
            }
            return Status.SUCCESS
        }

        override fun visitForEach(ctx: ForEachContext): Status {
            val values: List<Map<String, ApmType>> = readValues(ctx)
            for ((index, value) in values.withIndex()) {
                if (shouldVisitNextChild()) {
                    try {
                        executionContext.createLocalContext()
                        val valueStr = value.map { it.key + "=" + it.value }
                            .joinToString()
                        progress(ctx, Status.SUCCESS, "for-each", "$index. Begin: $valueStr")
                        value.forEach { (k, v) -> executionContext.setVariable(k, v) }
                        visit(ctx.body())
                        progress(ctx, Status.SUCCESS, "for-each", "$index. End")
                    } finally {
                        executionContext.removeLocalContext()
                    }
                }
            }
            return Status.SUCCESS
        }

        override fun visitRunScript(ctx: RunScriptContext): Status {
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
            return Status.SUCCESS
        }

        override fun visitGenericCommand(ctx: GenericCommandContext): Status {
            val commandName = getIdentifier(ctx.commandName().identifier()).uppercase()
            val arguments = executionContext.resolveArguments(ctx.complexArguments())
            return visitGenericCommand(ctx, commandName, arguments, ctx.body())
        }

        override fun visitAllowDenyCommand(ctx: AllowDenyCommandContext): Status {
            val commandName = if (ctx.ALLOW() != null) "ALLOW" else "DENY"
            val argument = executionContext.resolveArgument(ctx.argument())
            val arguments = executionContext.resolveArguments(ctx.complexArguments())
            val required = if (ctx.ON() == null) {
                listOf(argument) + arguments.required
            } else {
                arguments.required + argument
            }
            val newArguments = Arguments(required, arguments.named, arguments.flags)
            return visitGenericCommand(ctx, commandName, newArguments)
        }

        private fun visitGenericCommand(
            ctx: ParserRuleContext, commandName: String, arguments: Arguments, body: BodyContext? = null
        ): Status {
            return if (validateOnly) {
                visitGenericCommandValidateMode(ctx, commandName, arguments, body)
            } else {
                visitGenericCommandRunMode(ctx, commandName, arguments, body)
            }
        }

        private fun visitGenericCommandRunMode(
            ctx: ParserRuleContext, commandName: String, arguments: Arguments, body: BodyContext?
        ): Status {
            try {
                if (body != null) {
                    executionContext.createLocalContext()
                }
                val status = actionInvoker.runAction(executionContext, commandName, arguments)
                if (status == Status.SUCCESS && body != null) {
                    visit(body)
                }
                return status
            } catch (e: ArgumentResolverException) {
                progress(ctx, Status.ERROR, commandName, "Action failed: ${e.message}")
            } finally {
                if (body != null) {
                    executionContext.removeLocalContext()
                }
            }
            return Status.ERROR
        }

        private fun visitGenericCommandValidateMode(
            ctx: ParserRuleContext, commandName: String, arguments: Arguments, body: BodyContext?
        ): Status {
            try {
                if (body != null) {
                    executionContext.createLocalContext()
                }
                try {
                    actionInvoker.runAction(executionContext, commandName, arguments)
                } catch (e: ArgumentResolverException) {
                    progress(ctx, Status.WARNING, commandName, "Couldn't invoke action: ${e.message}")
                }
                if (body != null) {
                    visit(body)
                }
            } finally {
                if (body != null) {
                    executionContext.removeLocalContext()
                }
            }
            return Status.SUCCESS
        }

        override fun visitImportScript(ctx: ImportScriptContext): Status {
            val result = ImportScript(executionContext).import(ctx)
            executionContext.variableHolder.setAll(result.variableHolder)
            progress(ctx, Status.SUCCESS, "import", result.toMessages())
            return Status.SUCCESS
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