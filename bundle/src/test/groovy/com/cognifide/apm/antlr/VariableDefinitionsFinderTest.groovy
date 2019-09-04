package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.parsedscript.ParsedScript
import com.cognifide.cq.cqsm.api.scripts.Script
import spock.lang.Specification

class VariableDefinitionsFinderTest extends Specification {

    def "find variable definitions" (){
        given:
        def underTest = new VariableDefinitionsFinder()

        def script = createScript()

        when:
        def result = underTest.find(script)
        def v = ApmType
        then:
        result == ["varInteger": new ApmInteger(1),
                   "varString":new ApmString("string"),
                   "varList" : new ApmList(["1", "2"]) ]
    }

    private ParsedScript createScript() {
        def content = """ DEFINE varInteger 1
                          DEFINE varString "string"  
                          DEFINE varList ["1", "2"]"""
        def script = Mock(Script)
        script.path >> "/dummy/path"
        script.data >> content

        new ParsedScript.Factory().create(script)
    }
}
