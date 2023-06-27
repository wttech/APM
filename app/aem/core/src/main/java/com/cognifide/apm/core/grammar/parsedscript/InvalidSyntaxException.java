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

import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token

class InvalidSyntaxException : RuntimeException {

    val recognizer: Recognizer<*, *>
    val offendingToken: Token?
    val line: Int
    val charPositionInLine: Int

    constructor(e: RecognitionException) : super(e) {
        this.recognizer = e.recognizer as Parser
        this.offendingToken = e.offendingToken
        this.line = offendingToken!!.line
        this.charPositionInLine = offendingToken.charPositionInLine
    }

    constructor(recognizer: Recognizer<*, *>, line: Int, charPositionInLine: Int) {
        this.recognizer = recognizer
        this.offendingToken = null
        this.line = line
        this.charPositionInLine = charPositionInLine
    }
}
