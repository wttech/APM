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

package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.ApmLangParser.DefineVariableContext
import com.cognifide.apm.antlr.ApmLangParser.ForEachContext
import com.cognifide.apm.antlr.argument.ArgumentResolverException
import com.cognifide.apm.antlr.executioncontext.ExecutionContext
import com.cognifide.apm.antlr.executioncontext.ExecutionContextException
import com.cognifide.apm.antlr.parsedscript.InvalidSyntaxException
import com.cognifide.apm.antlr.parsedscript.InvalidSyntaxMessageFactory
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.api.logger.Status
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import org.apache.sling.api.resource.ResourceResolver

class ScriptRunner(
        private val scriptFinder: ScriptFinder,
        private val resourceResolver: ResourceResolver,
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
        } catch (e: ExecutionContextException) {
            progress.addEntry(Status.ERROR, e.message)
        }
        return progress
    }

    private inner class Executor(private val executionContext: ExecutionContext) : ApmLangBaseVisitor<Unit>() {

        override fun visitDefineVariable(ctx: DefineVariableContext) {
            val variableName = ctx.IDENTIFIER().toString()
            val variableValue = executionContext.resolveArgument(ctx.argument())
            executionContext.setVariable(variableName, variableValue)
            info("define", "Defined variable: $variableName= $variableValue")
        }

        override fun visitForEach(ctx: ForEachContext) {
            val index = ctx.IDENTIFIER().toString()
            val values: List<ApmValue> = readValues(ctx)
            for ((iteration, value) in values.withIndex()) {
                try {
                    executionContext.createLocalContext()
                    info("foreach", "$iteration. Begin: $index= $value")
                    executionContext.setVariable(index, value)
                    visit(ctx.body())
                    info("foreach", "$iteration. End")
                } finally {
                    executionContext.removeLocalContext()
                }
            }
        }

        private fun readValues(ctx: ForEachContext): List<ApmValue> {
            val variableValue = executionContext.resolveArgument(ctx.argument())
            return when (variableValue) {
                is ApmList -> variableValue.list.map { ApmString(it) }
                is ApmEmpty -> listOf()
                else -> listOf(variableValue as ApmValue)
            }
        }

        override fun visitGenericCommand(ctx: ApmLangParser.GenericCommandContext) {
            val commandName = getCommandName(ctx).toUpperCase()
            val arguments = executionContext.resolveArguments(ctx.arguments())
            actionInvoker.runAction(executionContext.progress, commandName, arguments)
        }

        private fun getCommandName(ctx: ApmLangParser.GenericCommandContext) = when {
            ctx.commandName().IDENTIFIER() != null -> ctx.commandName().IDENTIFIER().toString()
            ctx.commandName().EXTENDED_IDENTIFIER() != null -> ctx.commandName().EXTENDED_IDENTIFIER().toString()
            else -> throw RuntimeException("Cannot resolve command's name")
        }

        private fun info(command: String, details: String = "") {
            executionContext.progress.addEntry(Status.SUCCESS, details, command)
        }
    }
}