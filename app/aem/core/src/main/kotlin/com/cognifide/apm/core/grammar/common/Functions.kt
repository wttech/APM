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

package com.cognifide.apm.core.grammar.common

import com.cognifide.apm.core.grammar.ScriptExecutionException
import com.cognifide.apm.core.grammar.argument.toPlainString

fun getIdentifier(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.IdentifierContext) = when {
    ctx.IDENTIFIER() != null -> ctx.IDENTIFIER().toString()
    ctx.EXTENDED_IDENTIFIER() != null -> ctx.EXTENDED_IDENTIFIER().toString()
    else -> throw ScriptExecutionException("Cannot resolve identifier")
}

fun getIdentifier(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.VariableIdentifierContext) = when {
    ctx.IDENTIFIER() != null -> ctx.IDENTIFIER().toString()
    ctx.VARIABLE_IDENTIFIER() != null -> ctx.VARIABLE_IDENTIFIER().toString()
    else -> throw ScriptExecutionException("Cannot resolve identifier")
}

fun getPath(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.PathContext) = when {
    ctx.STRING_LITERAL() != null -> ctx.STRING_LITERAL().toPlainString()
    ctx.PATH_IDENTIFIER() != null -> ctx.PATH_IDENTIFIER().toString()
    else -> throw ScriptExecutionException("Cannot resolve path")
}

fun getCommandName(ctx: com.cognifide.apm.core.grammar.antlr.ApmLangParser.CommandNameContext) = when {
    ctx.identifier() != null -> getIdentifier(ctx.identifier())
    ctx.ALLOW() != null -> ctx.ALLOW().toString()
    ctx.DENY() != null -> ctx.DENY().toString()
    else -> throw ScriptExecutionException("Cannot resolve command name")
}
