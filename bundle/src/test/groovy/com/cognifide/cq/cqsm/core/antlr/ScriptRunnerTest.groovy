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
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters
import com.cognifide.cq.cqsm.core.loader.ScriptTree
import com.cognifide.cq.cqsm.core.macro.MacroRegister
import com.cognifide.cq.cqsm.core.macro.MacroRegistrar
import spock.lang.Specification

class ScriptRunnerTest extends Specification {

    def "run macros"() {
        given:
        def parser = ApmLangParserHelper.createParserUsingFile("/macros.apm")
        def scriptExecutor = new ScriptRunner(createActionInvoker())
        def scriptTree = new ScriptTree(parser.apm(), Collections.emptyMap())
        def macroRegister = new MacroRegistrar().buildMacroRegister(scriptTree)
        def scriptContext = new ScriptContext("user", macroRegister, scriptTree)

        when:
        def result = scriptExecutor.execute(scriptContext)

        then:
        def commands = result.entries.collect { it.command }
        commands == ["Executing command ALLOW path001 WRITE",
                     "Executing command ALLOW path002 WRITE",
                     "Executing command ALLOW path3 WRITE",
                     "Executing command ALLOW path001 READ",
                     "Executing command DENY path2 READ",
                     "Executing command ALLOW path3 READ",
                     "Executing command ALLOW path001 DELETE",
                     "Executing command ALLOW path002 DELETE",
                     "Executing command DENY path003 DELETE"]
    }

    def "run example"() {
        given:
        def parser = ApmLangParserHelper.createParserUsingFile("/example.cqsm")
        def scriptExecutor = new ScriptRunner(createActionInvoker())
        def scriptTree = new ScriptTree(parser.apm(), Collections.emptyMap())
        def scriptContext = new ScriptContext("user", new MacroRegister(), scriptTree)

        when:
        def result = scriptExecutor.execute(scriptContext)

        then:
        result != []
    }

    def createActionInvoker() {
        return new ActionInvoker() {
            @Override
            void runAction(Progress progress, String commandName, Parameters parameters) {
                def command = String.format("Executing command %s %s %s", commandName,
                        parameters.getString(0, ""), parameters.getString(1, ""))
                progress.addEntry(command, Message.getInfoMessage(""), Status.SUCCESS)
            }
        }
    }
}
