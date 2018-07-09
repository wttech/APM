package com.cognifide.cq.cqsm

import com.cognifide.apm.antlr.ApmLangLexer
import com.cognifide.apm.antlr.ApmLangParser
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CodePointCharStream
import org.antlr.v4.runtime.CommonTokenStream

class ApmLangParserHelper {

    def static createParserUsingFile(String scriptPath) {
        CharStream charStream = CharStreams.fromStream(getClass().getResourceAsStream(scriptPath))
        return createParser(charStream)
    }

    def static createParserUsingScript(String scriptContent) {
        CharStream charStream = CharStreams.fromString(scriptContent)
        return createParser(charStream)
    }

    private static ApmLangParser createParser(CodePointCharStream charStream) {
        ApmLangLexer lexer = new ApmLangLexer(charStream)
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer)
        return new ApmLangParser(commonTokenStream)
    }
}
