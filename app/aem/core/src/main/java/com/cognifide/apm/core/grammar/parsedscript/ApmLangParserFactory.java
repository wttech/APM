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

package com.cognifide.apm.core.grammar.parsedscript

import org.antlr.v4.runtime.*

object ApmLangParserFactory {

    fun createParserForScriptContent(scriptContent: String): com.cognifide.apm.core.grammar.antlr.ApmLangParser {
        val charStream = CharStreams.fromString(scriptContent)
        val lexer = com.cognifide.apm.core.grammar.antlr.ApmLangLexer(charStream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(LexerErrorListener())
        val commonTokenStream = CommonTokenStream(lexer)
        val apmLangParser = com.cognifide.apm.core.grammar.antlr.ApmLangParser(commonTokenStream)
        apmLangParser.removeErrorListeners()
        apmLangParser.addErrorListener(ParserErrorListener())
        apmLangParser.errorHandler = ErrorStrategy()
        return apmLangParser
    }

    private class ErrorStrategy : DefaultErrorStrategy() {

        override fun recover(recognizer: Parser, e: RecognitionException?) {
            throw InvalidSyntaxException(e!!)
        }

        @Throws(RecognitionException::class)
        override fun recoverInline(recognizer: Parser): Token {
            throw InvalidSyntaxException(InputMismatchException(recognizer))
        }

        override fun sync(recognizer: Parser) {}
    }

    private class LexerErrorListener : BaseErrorListener() {

        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int,
                                 msg: String?, e: RecognitionException?) {
            throw InvalidSyntaxException(recognizer!!, line, charPositionInLine)
        }
    }

    private class ParserErrorListener : BaseErrorListener() {

        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int,
                                 msg: String?, e: RecognitionException?) {
            throw InvalidSyntaxException(e!!)
        }
    }
}
