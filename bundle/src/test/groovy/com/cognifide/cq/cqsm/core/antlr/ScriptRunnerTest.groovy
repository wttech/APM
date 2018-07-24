package com.cognifide.cq.cqsm.core.antlr

import com.cognifide.apm.antlr.ApmLangParser
import com.cognifide.cq.cqsm.ApmLangParserHelper
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
        def scriptContext = new ScriptContext(macroRegister, scriptTree)

        when:
        def result = scriptExecutor.execute(scriptContext)

        then:
        result == ["Executing command ALLOW \$path1 WRITE",
                   "Executing command ALLOW \$path2 WRITE",
                   "Executing command ALLOW path3 WRITE",
                   "Executing command ALLOW \$path1 READ",
                   "Executing command DENY path2 READ",
                   "Executing command ALLOW path3 READ",
                   "Executing command ALLOW \$path1 DELETE",
                   "Executing command ALLOW \$path2 DELETE",
                   "Executing command DENY \$path3 DELETE"]
    }

    def "run example"() {
        given:
        def parser = ApmLangParserHelper.createParserUsingFile("/example.cqsm")
        def scriptExecutor = new ScriptRunner(createActionInvoker())
        def scriptTree = new ScriptTree(parser.apm(), Collections.emptyMap())
        def scriptContext = new ScriptContext(new MacroRegister(), scriptTree)

        when:
        def result = scriptExecutor.execute(scriptContext)

        then:
        result != []
    }

    def createActionInvoker() {
        return new ActionInvoker() {
            @Override
            List<String> runAction(ApmLangParser.GenericCommandContext ctx, String command, String commandName, Parameters parameters) {
                return Collections
                        .singletonList(String.format("Executing command %s", command))
            }
        }
    }
}
