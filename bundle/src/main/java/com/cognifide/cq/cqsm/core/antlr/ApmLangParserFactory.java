/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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

package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.apm.antlr.ApmLangLexer;
import com.cognifide.apm.antlr.ApmLangParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApmLangParserFactory {

  public static ApmLangParser createParserForScript(String scriptContent) {
    CharStream charStream = CharStreams.fromString(scriptContent);
    ApmLangLexer lexer = new ApmLangLexer(charStream);
    lexer.removeErrorListeners();
    lexer.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
          String msg, RecognitionException e) {
        throw new InvalidSyntaxException(recognizer, line, charPositionInLine);
      }
    });
    CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
    ApmLangParser apmLangParser = new ApmLangParser(commonTokenStream);
    apmLangParser.removeErrorListeners();
    apmLangParser.addErrorListener(new ErrorListener());
    apmLangParser.setErrorHandler(new ErrorStrategy());
    return apmLangParser;
  }

  private static class ErrorStrategy extends DefaultErrorStrategy {

    @Override
    public void recover(Parser recognizer, RecognitionException e) {
      throw new InvalidSyntaxException(e);
    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
      throw new InvalidSyntaxException(new InputMismatchException(recognizer));
    }

    @Override
    public void sync(Parser recognizer) {
    }
  }

  private static class ErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
        String msg, RecognitionException e) {
      throw new InvalidSyntaxException(e);
    }
  }
}
