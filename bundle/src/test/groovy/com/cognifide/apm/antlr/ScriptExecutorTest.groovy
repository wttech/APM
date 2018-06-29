package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.ApmLangParserHelper
import spock.lang.Specification

class ScriptExecutorTest extends Specification {

    def "registers macros"() {
        given:
        def parser = ApmLangParserHelper.createParserUsingFile("/macros.apm")
        def scriptExecutor = new ScriptExecutor()

        when:
        def result = scriptExecutor.execute(parser.apm())

        then:
        result == ["Executing command ALLOW with parameters [path001, WRITE]",
                   "Executing command ALLOW with parameters [path002, WRITE]",
                   "Executing command ALLOW with parameters [path3, WRITE]",
                   "Executing command ALLOW with parameters [path001, READ]",
                   "Executing command DENY path2 READ \r\n",
                   "Executing command ALLOW with parameters [path3, READ]",
                   "Executing command ALLOW with parameters [path001, DELETE]",
                   "Executing command ALLOW with parameters [path002, DELETE]",
                   "Executing command DENY \${path3} DELETE \r\n"]
    }

    def "run example"() {
        given:
        def parser = ApmLangParserHelper.createParserUsingFile("/example.cqsm")
        def scriptExecutor = new ScriptExecutor()

        when:
        def result = scriptExecutor.execute(parser.apm())

        then:
        result != []
    }
}
