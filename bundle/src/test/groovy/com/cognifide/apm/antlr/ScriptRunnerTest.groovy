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

import com.cognifide.apm.antlr.argument.Arguments
import com.cognifide.cq.cqsm.api.logger.Message
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.api.logger.Status
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import com.cognifide.cq.cqsm.core.progress.ProgressImpl
import org.apache.commons.io.IOUtils
import org.apache.sling.api.resource.ResourceResolver
import spock.lang.Specification

class ScriptRunnerTest extends Specification {

    def scriptFinder = Mock(ScriptFinder)
    def resourceResolver = Mock(ResourceResolver)
    def scriptExecutor = new ScriptRunner(scriptFinder, resourceResolver, createActionInvoker())

    def "run foreach"() {
        given:
        Script script = createScript("/foreach.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command SHOW 'a/c'",
                     "Executing command SHOW 'a/d'",
                     "Executing command SHOW 'b/c'",
                     "Executing command SHOW 'b/d'"]
    }

    def "run define"() {
        given:
        Script script = createScript("/define.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command SHOW 'global'",
                     "Executing command SHOW 'global'",
                     "Executing command SHOW '1. foreach'",
                     "Executing command SHOW '2. foreach'",
                     "Executing command SHOW '1. foreach'",
                     "Executing command SHOW 'global'"]
    }

    private Script createScript(String file) {
        def content = IOUtils.toString(getClass().getResourceAsStream(file))
        def script = Mock(Script)
        script.path >> "/conf/apm/scripts/main.apm"
        script.data >> content
        return script
    }

    private static ActionInvoker createActionInvoker() {
        new ActionInvoker() {
            @Override
            void runAction(Progress progress, String commandName, Arguments arguments) {
                def command = new StringBuilder("Executing command ")
                command.append(commandName)
                arguments.required.each {
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
