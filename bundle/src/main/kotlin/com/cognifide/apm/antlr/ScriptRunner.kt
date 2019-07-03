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
import com.cognifide.cq.cqsm.api.logger.Message
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.api.logger.Status

class ScriptRunner(private val actionInvoker: ActionInvoker) {

    fun execute(executionContext: ExecutionContext): Progress {
        val executor = Executor(executionContext)
        executor.visit(executionContext.root)
        return executionContext.progress
    }

    private inner class Executor(private val executionContext: ExecutionContext) : ApmLangBaseVisitor<Unit>() {

        override fun visitDefineVariable(ctx: DefineVariableContext) {
            val variableName = ctx.IDENTIFIER().toString()
            val variableValue = executionContext.argumentResolver.resolve(ctx.argument())
            executionContext.variableHolder[variableName] = variableValue
            info("define", "Defined variable: $variableName = $variableValue")
        }

        override fun visitForEach(ctx: ApmLangParser.ForEachContext) {
            try {
                executionContext.variableHolder.createLocalContext()
                val variableName = ctx.IDENTIFIER().toString()
                val variableValue = executionContext.argumentResolver.resolve(ctx.argument())
                val values: List<ApmValue> = when (variableValue) {
                    is ApmList -> variableValue.list.map { ApmString(it) }
                    is ApmEmpty -> listOf()
                    else -> listOf(variableValue as ApmValue)
                }
                var i = 1
                info("foreach", "Begin")
                for (value in values) {
                    info("foreach", "Iteration: $i")
                    executionContext.variableHolder[variableName] = value
                    visit(ctx.body())
                    i++
                }
                info("foreach", "End")
            } finally {
                executionContext.variableHolder.removeLocalContext()
            }
        }

        override fun visitGenericCommand(ctx: ApmLangParser.GenericCommandContext) {
            val commandName = ctx.IDENTIFIER().toString().toUpperCase()
            val arguments = executionContext.argumentResolver.resolve(ctx.arguments())
            actionInvoker.runAction(executionContext.progress, commandName, arguments)
        }

        private fun info(shortInfo: String, details: String = "") {
            executionContext.progress.addEntry(shortInfo, Message.getInfoMessage(details), Status.SUCCESS)
        }
    }
}