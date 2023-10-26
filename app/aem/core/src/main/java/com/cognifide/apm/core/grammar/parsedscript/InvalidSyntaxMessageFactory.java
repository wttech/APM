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

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.apache.commons.lang3.StringUtils;

public final class InvalidSyntaxMessageFactory {

  private InvalidSyntaxMessageFactory() {
    // intentionally empty
  }

  public static List<String> detailedSyntaxError(InvalidSyntaxException e) {
    return underlineError(e.getRecognizer(), e.getOffendingToken(), e.getLine(), e.getCharPositionInLine());
  }

  private static List<String> underlineError(Recognizer<?, ?> recognizer, Token offendingToken, int line, int charPositionInLine) {
    String errorLine = getErrorLine(recognizer, line);
    String invalidLine = String.format("Invalid line [%d:%d]: %s", line, charPositionInLine, errorLine);
    if (offendingToken != null && StringUtils.isNotBlank(offendingToken.getText())) {
      return ImmutableList.of(invalidLine, String.format("Invalid sequence: %s", offendingToken.getText()));
    } else {
      return Collections.singletonList(invalidLine);
    }
  }

  private static String getErrorLine(Recognizer<?, ?> recognizer, int line) {
    String input = toString(recognizer.getInputStream());
    String[] lines = input.split("\n");
    return lines[line - 1];
  }

  private static String toString(IntStream inputStream) {
    return Optional.of(inputStream)
        .filter(CommonTokenStream.class::isInstance)
        .map(CommonTokenStream.class::cast)
        .map(CommonTokenStream::getTokenSource)
        .map(TokenSource::getInputStream)
        .map(CharStream::toString)
        .orElse(inputStream.toString());
  }
}
