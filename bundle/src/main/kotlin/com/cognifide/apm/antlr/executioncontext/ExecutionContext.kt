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

package com.cognifide.apm.antlr.executioncontext

import com.cognifide.apm.antlr.ApmLangParser.*
import com.cognifide.apm.antlr.ApmType
import com.cognifide.apm.antlr.argument.ArgumentResolver
import com.cognifide.apm.antlr.argument.Arguments
import com.cognifide.apm.antlr.common.StackWithRoot
import com.cognifide.apm.antlr.parsedscript.ParsedScript
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.resource.ResourceResolver

class ExecutionContext private constructor(
        private val scriptFinder: ScriptFinder,
        private val resourceResolver: ResourceResolver,
        val root: ParsedScript,
        val progress: Progress) {

    private val parsedScripts: MutableMap<String, ParsedScript> = mutableMapOf()
    private var runScripts: StackWithRoot<RunScript> = StackWithRoot(RunScript(root))

    private val currentRunScript: RunScript
        get() = runScripts.peek()
    val variableHolder: VariableHolder
        get() = currentRunScript.variableHolder
    val argumentResolver: ArgumentResolver
        get() = ArgumentResolver(variableHolder)

    init {
        registerScript(root)
    }

    companion object {
        @JvmStatic
        fun create(scriptFinder: ScriptFinder, resourceResolver: ResourceResolver, script: Script, progress: Progress): ExecutionContext {
            return ExecutionContext(scriptFinder, resourceResolver, ParsedScript.create(script), progress)
        }
    }

    fun loadScript(path: String): ParsedScript {
        val absolutePath = resolveAbsolutePath(path)
        return parsedScripts[absolutePath] ?: fetchScript(absolutePath)
    }

    fun createScriptContext(parsedScript: ParsedScript) {
        runScripts.push(RunScript(parsedScript))
    }

    fun createLocalContext() {
        variableHolder.createLocalContext()
    }

    fun removeScriptContext() {
        runScripts.pop()
    }

    fun removeLocalContext() {
        variableHolder.removeLocalContext()
    }

    fun setVariable(key: String, value: ApmType) {
        variableHolder[key] = value
    }

    fun getVariable(key: String): ApmType? {
        return variableHolder[key]
    }

    fun resolveArguments(arguments: ComplexArgumentsContext): Arguments {
        return argumentResolver.resolve(arguments)
    }

    fun resolveArguments(arguments: NamedArgumentsContext): Arguments {
        return argumentResolver.resolve(arguments)
    }

    fun resolveArgument(argument: ArgumentContext): ApmType {
        return argumentResolver.resolve(argument)
    }

    private fun fetchScript(path: String): ParsedScript {
        val script = scriptFinder.find(path, resourceResolver)
                ?: throw ExecutionContextException("Script not found $path")
        val parsedScript = ParsedScript.create(script)
        registerScript(parsedScript)
        return parsedScript
    }

    private fun registerScript(parsedScript: ParsedScript) {
        parsedScripts[parsedScript.path] = parsedScript
    }

    private fun resolveAbsolutePath(path: String): String {
        return if (path.startsWith("/")) {
            path
        } else {
            StringUtils.substringBeforeLast(runScripts.peek().path, "/") + "/" + path
        }
    }
}
