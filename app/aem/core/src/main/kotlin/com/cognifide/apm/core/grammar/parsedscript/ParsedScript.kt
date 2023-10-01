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

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.ApmContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ParsedScript(val script: Script, val apm: ApmContext) {

    val path: String
        get() = script.path

    companion object Factory {
        fun create(script: Script): ParsedScript {
            val logger: Logger = LoggerFactory.getLogger(ParsedScript::class.java)
            logger.warn("Script parsing {}", script.path)
            val apmLangParser = ApmLangParserFactory.createParserForScriptContent(script.data)
            return ParsedScript(script, apmLangParser.apm())
        }
    }
}