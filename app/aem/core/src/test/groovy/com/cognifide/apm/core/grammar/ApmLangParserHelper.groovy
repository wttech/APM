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


import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class ApmLangParserHelper {

    def static createParserUsingFile(String scriptPath) {
        CharStream charStream = CharStreams.fromStream(ApmLangParserHelper.class.getResourceAsStream(scriptPath))
        return createParser(charStream)
    }

    def static createParserUsingScript(String scriptContent) {
        CharStream charStream = CharStreams.fromString(scriptContent)
        return createParser(charStream)
    }

    private static com.cognifide.apm.core.grammar.antlr.ApmLangParser createParser(CharStream charStream) {
        com.cognifide.apm.core.grammar.antlr.ApmLangLexer lexer = new com.cognifide.apm.core.grammar.antlr.ApmLangLexer(charStream)
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer)
        return new com.cognifide.apm.core.grammar.antlr.ApmLangParser(commonTokenStream)
    }
}
