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

package com.cognifide.apm.core.grammar.common;

import com.cognifide.apm.core.grammar.ScriptExecutionException;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser;
import org.apache.commons.lang3.StringUtils;

public final class Functions {

  private Functions() {
    // intentionally empty
  }

  public static String getIdentifier(ApmLangParser.IdentifierContext ctx) {
    if (ctx.IDENTIFIER() != null) {
      return ctx.IDENTIFIER().toString();
    } else if (ctx.EXTENDED_IDENTIFIER() != null) {
      return ctx.EXTENDED_IDENTIFIER().toString();
    }
    throw new ScriptExecutionException("Cannot resolve identifier");
  }

  public static String getIdentifier(ApmLangParser.VariableIdentifierContext ctx) {
    if (ctx.IDENTIFIER() != null) {
      return ctx.IDENTIFIER().toString();
    } else if (ctx.VARIABLE_IDENTIFIER() != null) {
      return ctx.VARIABLE_IDENTIFIER().toString();
    }
    throw new ScriptExecutionException("Cannot resolve identifier");
  }

  public static String getKey(ApmLangParser.StructureKeyContext ctx) {
    if (ctx.IDENTIFIER() != null) {
      return ctx.IDENTIFIER().toString();
    } else if (ctx.STRING_LITERAL() != null) {
      return toPlainString(ctx.STRING_LITERAL().toString());
    }
    throw new ScriptExecutionException("Cannot resolve key");
  }

  public static String getPath(ApmLangParser.PathContext ctx) {
    if (ctx.STRING_LITERAL() != null) {
      return toPlainString(ctx.STRING_LITERAL().toString());
    } else if (ctx.PATH_IDENTIFIER() != null) {
      return ctx.PATH_IDENTIFIER().toString();
    }
    throw new ScriptExecutionException("Cannot resolve path");
  }

  public static String toPlainString(String value) {
    if (StringUtils.startsWith(value, "\"")) {
      return StringUtils.strip(value, "\"");
    } else if (StringUtils.startsWith(value, "'")) {
      return StringUtils.strip(value, "'");
    }
    return value;
  }
}
