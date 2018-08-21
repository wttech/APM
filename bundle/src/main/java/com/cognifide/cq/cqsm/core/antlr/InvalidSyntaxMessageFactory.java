package com.cognifide.cq.cqsm.core.antlr;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvalidSyntaxMessageFactory {

  public static String generalSyntaxError(InvalidSyntaxException e) {
    return String.format("Error at %d:%d", e.getLine(), e.getCharPositionInLine());
  }

  public static String detailedSyntaxError(InvalidSyntaxException e) {
    return underlineError(e.getRecognizer(), e.getOffendingToken(), e.getLine(), e.getCharPositionInLine());
  }

  private static String underlineError(Recognizer recognizer, Token offendingToken, int line, int charPositionInLine) {
    String errorLine = getErrorLine(recognizer, line);
    StringBuilder builder = new StringBuilder();
    builder.append("Invalid line: ");
    builder.append(errorLine);
    builder.append("\n");
    int length = invalidSequenceLength(offendingToken);
    builder.append("Invalid sequence: ");
    builder.append(errorLine, charPositionInLine, charPositionInLine + length);
    return builder.toString();
  }

  private static String getErrorLine(Recognizer recognizer, int line) {
    String input = toString(recognizer.getInputStream());
    String[] lines = input.split("\n");
    return lines[line - 1];
  }

  private static String toString(IntStream inputStream) {
    if (inputStream instanceof CommonTokenStream) {
      CommonTokenStream tokens = (CommonTokenStream) inputStream;
      return tokens.getTokenSource().getInputStream().toString();
    } else {
      return inputStream.toString();
    }
  }

  private static int invalidSequenceLength(Token offendingToken) {
    if (offendingToken != null) {
      int start = offendingToken.getStartIndex();
      int stop = offendingToken.getStopIndex();
      if (start >= 0 && stop >= start) {
        return Math.max(stop - start, 1);
      }
    }
    return 1;
  }
}
