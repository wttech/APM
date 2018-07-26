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
