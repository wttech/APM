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
import com.cognifide.apm.core.crypto.DecryptionService
import com.cognifide.apm.core.grammar.ApmType.ApmString
import com.cognifide.apm.core.grammar.argument.Arguments
import com.cognifide.apm.core.grammar.datasource.DataSource
import com.cognifide.apm.core.grammar.datasource.DataSourceInvoker
import com.cognifide.apm.core.grammar.executioncontext.ExternalExecutionContext
import com.cognifide.apm.core.progress.ProgressImpl
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.reflect.FieldUtils
import org.apache.sling.api.resource.ResourceResolver
import spock.lang.Specification

class ScriptRunnerTest extends Specification {

    def scriptFinder = Mock(ScriptFinder)
    def resourceResolver = Mock(ResourceResolver)
    def scriptExecutor = new ScriptRunner(scriptFinder, resourceResolver, false, createActionInvoker(), createDataSourceInvoker())

    def "run for-each"() {
        given:
        Script script = createScript("/foreach.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command SHOW \"a/c\"",
                     "Executing command SHOW \"a/d\"",
                     "Executing command SHOW \"b/c\"",
                     "Executing command SHOW \"b/d\"",
                     "Executing command SHOW \"a/b/e/f\"",
                     "Executing command SHOW \"a/b/g/h\"",
                     "Executing command SHOW \"c/d/e/f\"",
                     "Executing command SHOW \"c/d/g/h\"",
                     "Executing command SHOW \"A/B/E/F\"",
                     "Executing command SHOW \"A/B/G/H\"",
                     "Executing command SHOW \"C/D/E/F\"",
                     "Executing command SHOW \"C/D/G/H\""]
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
        commands == ["Executing command SHOW \"global\"",
                     "Executing command SHOW \"global\"",
                     "Executing command SHOW \"1. for-each\"",
                     "Executing command SHOW \"2. for-each\"",
                     "Executing command SHOW \"1. for-each\"",
                     "Executing command SHOW \"global\"",
                     "Executing command SHOW \"a\"",
                     "Executing command SHOW \"b\"",
                     "Executing command SHOW [\"a\", \"b\"]",
                     "Executing command SHOW \"a\"",
                     "Executing command SHOW \"b\"",
                     "Executing command SHOW [\"c\", \"d\"]",
                     "Executing command SHOW [\n\t[\"a\", \"b\"],\n\t[\"c\", \"d\"]\n]",
                     "Executing command SHOW [1, 2, 3]",
                     "Executing command SHOW [\"a\", \"b\", 1, 2]",
                     "Executing command SHOW {\n\tx: \"a\",\n\ty: 1,\n\tz: [\"c\", 1],\n\tt: \"t\"\n}",
                     "Executing command SHOW 1",
                     "Executing command SHOW 1",
                     "Executing command SHOW \"t\"",
                     "Executing command SHOW \"t\"",
                     "Executing command SHOW [3, \"ab\"]",
                     "Executing command SHOW [\"a\", \"b\", \"c\", \"d\", 1, 2]",
                     "Executing command SHOW [\n\t[\"a\", \"b\"],\n\t[\"c\", \"d\"]\n]",
                     "Executing command SHOW 3",
                     "Executing command SHOW 6",
                     "Executing command SHOW \"a\"",
                     "Executing command SHOW \"b\"",
                     "Executing command SHOW \"C\"",
                     "Executing command SHOW \"AB1\"",
                     "Executing command SHOW \"Ab1\"",
                     "Executing command SHOW \"const\"",
                     "Executing command SHOW \"CONST\""]
    }

    def "run define map"() {
        given:
        Script script = createScript("/define-map.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command SHOW 1",
                     "Executing command SHOW 1",
                     "Executing command SHOW 1",
                     "Executing command SHOW 1",
                     "Executing command SHOW \"a\"",
                     "Executing command SHOW 2",
                     "Executing command SHOW \"b\"",
                     "Executing command SHOW 2",
                     "Executing command SHOW \"b\"",
                     "Executing command SHOW \"c\"",
                     "Executing command SHOW \"d\"",
                     "Executing command SHOW \"e\"",
                     "Executing command SHOW \"f\"",
                     "Executing command SHOW \"c\"",
                     "Executing command SHOW \"d\"",
                     "Executing command SHOW \"e\"",
                     "Executing command SHOW \"f\""]
    }

    def "run macro"() {
        given:
        Script script = createScript("/run-macro.apm")
        scriptFinder.find("/macro.apm", resourceResolver) >> createScript("/macro.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command SHOW \"abc\"",
                     "Executing command SHOW \"123\""]
    }

    def "run import"() {
        given:
        Script script = createScript("/import.apm")
        scriptFinder.find("/import-define.apm", resourceResolver) >> createScript("/import-define.apm")
        scriptFinder.find("/import-deep-define.apm", resourceResolver) >> createScript("/import-deep-define.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        result.entries.size() == 7
        result.entries[0].messages ==
                ["Import from script /import-define.apm. Notice, only DEFINE actions were processed!",
                 "Imported variable: var=\"imported val\""]

        result.entries[1].messages ==
                ["Import from script /import-define.apm. Notice, only DEFINE actions were processed!",
                 "Imported variable: namespace={var: \"imported val\"}"]

        result.entries[2].messages ==
                ["Import from script /import-deep-define.apm. Notice, only DEFINE actions were processed!",
                 "Imported variable: deepNamespace={\n\tdeeperNamespace: {var: \"imported val\"},\n\tdeepVar: \"imported val + imported val\"\n}"]

        result.entries[3].command == "Executing command SHOW \"imported val\""
        result.entries[4].command == "Executing command SHOW \"imported val\""
        result.entries[5].command == "Executing command SHOW \"imported val\""
        result.entries[6].command == "Executing command SHOW \"imported val + imported val\""
    }

    def "run script filename.apm"() {
        given:
        Script script = createScript("/filename.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command CREATE-USER \"author\"",
                     "Executing command CREATE-GROUP \"authors\"",
                     "Executing command FOR-GROUP \"authors\"",
                     "Executing command ALLOW \"/content\" [\"jcr:read\"]",
                     "Executing command ALLOW \"/content/foo/bar\" [\"ALL\"]",
                     "Executing command DENY \"/content/foo/bar/foo\" [\"MODIFY\", \"DELETE\"]",
                     "Executing command DENY \"/content/foo/bar/foo/bar\" [\"MODIFY\", \"DELETE\"]"]
    }

    def "run script content.apm"() {
        given:
        Script script = createScript("/content.apm")

        when:
        def result = scriptExecutor.execute(script, new ProgressImpl(""))

        then:
        def commands = result.entries
                .collect { it.command }
                .findAll { it.startsWith("Executing") }
        commands == ["Executing command CREATE-USER \"author\"",
                     "Executing command CREATE-GROUP \"authors\"",
                     "Executing command FOR-GROUP \"authors\"",
                     "Executing command ALLOW \"/content/foo/bar\" [\"ALL\"]",
                     "Executing command DENY \"/content/foo/bar/foo/bar\" [\"MODIFY\", \"DELETE\"]"]
    }

    private Script createScript(String file) {
        def content = IOUtils.toString(getClass().getResourceAsStream(file))
        def script = Mock(Script)
        script.path >> ("/conf/apm/scripts" + file)
        script.data >> content
        return script
    }

    private static ActionInvoker createActionInvoker() {
        new ActionInvoker() {
            @Override
            Status runAction(ExternalExecutionContext context, String commandName, Arguments arguments) {
                def command = new StringBuilder("Executing command ")
                command.append(commandName)
                arguments.required.each {
                    command.append(" ").append(it.toString())
                }
                context.progress.addEntry(Status.SUCCESS, "", command.toString())
                return Status.SUCCESS
            }
        }
    }

    private static DataSourceInvoker createDataSourceInvoker() {
        def dataSourceInvoker = new DataSourceInvoker()
        FieldUtils.writeField(dataSourceInvoker, "decryptionService", new DecryptionService(), true)
        def bindDataSource = DataSourceInvoker.class.getDeclaredMethod("bindDataSource", DataSource.class)
        bindDataSource.setAccessible(true)
        bindDataSource.invoke(dataSourceInvoker, new DataSource() {
            @Override
            String getName() {
                return "FUNC"
            }

            @Override
            ApmType determine(ResourceResolver resolver, List<Object> parameters) {
                return new ApmString(parameters.get(0))
            }
        })
        bindDataSource.invoke(dataSourceInvoker, new DataSource() {
            @Override
            String getName() {
                return "UPPER"
            }

            @Override
            ApmType determine(ResourceResolver resolver, List<Object> parameters) {
                return new ApmString(parameters.get(0).toUpperCase())
            }
        })
        bindDataSource.invoke(dataSourceInvoker, new DataSource() {
            @Override
            String getName() {
                return "CONST"
            }

            @Override
            ApmType determine(ResourceResolver resolver, List<Object> parameters) {
                return new ApmString("const")
            }
        })
        return dataSourceInvoker
    }
}
