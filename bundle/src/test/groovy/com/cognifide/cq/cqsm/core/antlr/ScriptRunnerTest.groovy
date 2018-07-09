package com.cognifide.cq.cqsm.core.antlr

import com.cognifide.apm.antlr.ApmLangParser
import com.cognifide.cq.cqsm.ApmLangParserHelper
import spock.lang.Specification

class ScriptRunnerTest extends Specification {

    def "run macros"() {
        given:
        def parser = ApmLangParserHelper.createParserUsingFile("/macros.apm")
        def scriptExecutor = new ScriptRunner(createActionInvoker())

        when:
        def result = scriptExecutor.execute(parser.apm())

        then:
        result == ["Executing command ALLOW \${path1} WRITE",
                   "Executing command ALLOW \${path2} WRITE",
                   "Executing command ALLOW path3 WRITE",
                   "Executing command ALLOW \${path1} READ",
                   "Executing command DENY path2 READ",
                   "Executing command ALLOW path3 READ",
                   "Executing command ALLOW \${path1} DELETE",
                   "Executing command ALLOW \${path2} DELETE",
                   "Executing command DENY \${path3} DELETE"]
    }

    def "run example"() {
        given:
        def parser = ApmLangParserHelper.createParserUsingFile("/example.cqsm")
        def scriptExecutor = new ScriptRunner(createActionInvoker())

        when:
        def result = scriptExecutor.execute(parser.apm())

        then:
        result != []
    }

    def createActionInvoker() {
        return new ActionInvoker() {
            @Override
            List<String> runAction(ApmLangParser.GenericCommandContext ctx, String command) {
                return Collections
                        .singletonList(String.format("Executing command %s", command))
            }
        }
    }
}
