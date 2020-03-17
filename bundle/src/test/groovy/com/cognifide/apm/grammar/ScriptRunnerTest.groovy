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

import com.cognifide.apm.grammar.argument.Arguments
import com.cognifide.apm.grammar.executioncontext.ExternalExecutionContext
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
    def scriptExecutor = new ScriptRunner(scriptFinder, resourceResolver, false, createActionInvoker())

    def "run for-each"() {
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
                     "Executing command SHOW '1. for-each'",
                     "Executing command SHOW '2. for-each'",
                     "Executing command SHOW '1. for-each'",
                     "Executing command SHOW 'global'"]
    }

    def "run import"() {
        given:
        Script script = createScript("/import.apm")
        scriptFinder.find("/import-define.apm", resourceResolver) >> createScript("/import-define.apm")
        scriptFinder.find("/import-deep-define.apm", resourceResolver) >> createScript("/import-deep-define.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        result.entries.size() == 3
        result.entries[0].messages ==
                ["Import from script /import-define.apm. Notice, only DEFINE actions were processed!",
                 "Imported variable: var= \"imported val\""]

        result.entries[1].messages ==
                ["Import from script /import-define.apm. Notice, only DEFINE actions were processed!",
                 "Imported variable: namespace_var= \"imported val\""]

        result.entries[2].messages ==
                ["Import from script /import-deep-define.apm. Notice, only DEFINE actions were processed!",
                 "Imported variable: deepNamespace_deeperNamespace_var= \"imported val\"",
                 "Imported variable: deepNamespace_deepVar= \"imported val + imported val\""]
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
            void runAction(ExternalExecutionContext context, String commandName, Arguments arguments) {
                def command = new StringBuilder("Executing command ")
                command.append(commandName)
                arguments.required.each {
                    command.append(" ")
                            .append("'")
                            .append(it.getString())
                            .append("'")
                }
                context.progress.addEntry(Status.SUCCESS, "", command.toString())
            }
        }
    }
}
