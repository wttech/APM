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

package com.cognifide.apm.core.grammar.parsedscript;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class InvalidSyntaxException extends RuntimeException {

  private final Recognizer<?, ?> recognizer;

  private final Token offendingToken;

  private final int line;

  private final int charPositionInLine;

  public InvalidSyntaxException(RecognitionException e) {
    super(e);
    this.recognizer = e.getRecognizer();
    this.offendingToken = e.getOffendingToken();
    this.line = offendingToken.getLine();
    this.charPositionInLine = offendingToken.getCharPositionInLine();
  }

  public InvalidSyntaxException(Recognizer<?, ?> recognizer, int line, int charPositionInLine) {
    this.recognizer = recognizer;
    this.offendingToken = null;
    this.line = line;
    this.charPositionInLine = charPositionInLine;
  }

  public Recognizer<?, ?> getRecognizer() {
    return recognizer;
  }

  public Token getOffendingToken() {
    return offendingToken;
  }

  public int getLine() {
    return line;
  }

  public int getCharPositionInLine() {
    return charPositionInLine;
  }
}
