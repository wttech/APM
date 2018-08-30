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

package com.cognifide.cq.cqsm.core.antlr

import com.cognifide.cq.cqsm.ApmLangParserHelper
import com.cognifide.cq.cqsm.api.logger.Message
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.api.logger.Status
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters
import com.cognifide.cq.cqsm.core.antlr.type.ApmString
import com.cognifide.cq.cqsm.core.loader.ScriptTree
import com.cognifide.cq.cqsm.core.macro.MacroRegistrar
import spock.lang.Specification

class ScriptRunnerTest extends Specification {

    def scriptExecutor = new ScriptRunner(createActionInvoker())

    def "run macros"() {
        given:
        ScriptContext scriptContext = createScriptContext("/macros.apm")

        when:
        def result = scriptExecutor.execute(scriptContext)

        then:
        def commands = result.entries.collect { it.command }
        commands == ["Executing command ALLOW 'path001' 'WRITE'",
                     "Executing command ALLOW 'path002' 'WRITE'",
                     "Executing command ALLOW 'path3' 'WRITE'",
                     "Executing command ALLOW 'path001' 'READ'",
                     "Executing command DENY 'path2' 'READ'",
                     "Executing command ALLOW 'path3' 'READ'",
                     "Executing command ALLOW 'path001' 'DELETE'",
                     "Executing command ALLOW 'path002' 'DELETE'",
                     "Executing command DENY 'path003' 'DELETE'"]
    }

    def "run foreach"() {
        given:
        ScriptContext scriptContext = createScriptContext("/foreach.apm")

        when:
        def result = scriptExecutor.execute(scriptContext)

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command SHOW 'a/c'",
                     "Executing command SHOW 'a/d'",
                     "Executing command SHOW 'b/c'",
                     "Executing command SHOW 'b/d'",
                     "Executing command SEPARATE",
                     "Executing command SHOW 'a/c'",
                     "Executing command SHOW 'a/d'",
                     "Executing command SHOW 'b/c'",
                     "Executing command SHOW 'b/d'"]
    }

    def "run define"() {
        given:
        ScriptContext scriptContext = createScriptContext("/define.apm")

        when:
        def result = scriptExecutor.execute(scriptContext)

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command SHOW 'global'",
                     "Executing command SHOW 'global'",
                     "Executing command SHOW '1. foreach'",
                     "Executing command SHOW '2. foreach'",
                     "Executing command SHOW '1. foreach'",
                     "Executing command SHOW 'global'",
                     "Executing command SEPARATE",
                     "Executing command SHOW 'null'",
                     "Executing command SHOW '1. macro'",
                     "Executing command SHOW '2. macro'",
                     "Executing command SHOW '1. macro'",
                     "Executing command SHOW 'null'",
                     "Executing command SHOW 'global'"]
    }

    def "run example"() {
        given:
        ScriptContext scriptContext = createScriptContext("/example.cqsm")

        when:
        def result = scriptExecutor.execute(scriptContext)

        then:
        result != []
    }

    private ScriptContext createScriptContext(String file) {
        def parser = ApmLangParserHelper.createParserUsingFile(file)
        def script = Mock(Script)
        script.apm >> parser.apm()
        def scriptTree = new ScriptTree(script, Collections.emptyMap())
        def macroRegister = new MacroRegistrar().buildMacroRegister(scriptTree)
        def scriptContext = new ScriptContext("user", macroRegister, scriptTree)
        scriptContext
    }

    private ActionInvoker createActionInvoker() {
        new ActionInvoker() {
            @Override
            void runAction(Progress progress, String commandName, Parameters parameters) {
                def command = new StringBuilder("Executing command ")
                command.append(commandName)
                parameters.each {
                    command.append(" ")
                            .append("'")
                            .append(it.getString())
                            .append("'")
                }
                progress.addEntry(command.toString(), Message.getInfoMessage(""), Status.SUCCESS)
            }
        }
    }
}
