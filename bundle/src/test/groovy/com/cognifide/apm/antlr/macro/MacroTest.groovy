package com.cognifide.apm.antlr.macro

import com.cognifide.apm.antlr.ApmLangParserHelper
import spock.lang.Specification

class MacroTest extends Specification {

    def "registers macros"() {
        given:
        def parser = ApmLangParserHelper.createParserUsingFile("/macros.apm")
        def macroRegistrar = new MacroRegistrar()

        when:
        def result = macroRegistrar.findMacroDefinitions(new MacroRegister(), parser.apm())

        then:
        result.get("macro1") != null
        result.get("macro2") != null
        result.get("macro3") != null
        result.get("macro4") == null
    }
}
