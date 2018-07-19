package com.cognifide.cq.cqsm.core.antlr;

import org.antlr.v4.runtime.tree.TerminalNode;

public final class StringLiteral {

  public static String getValue(TerminalNode stringLiteralNode) {
    String stringLiteral = stringLiteralNode.toString();
    return stringLiteral.substring(1, stringLiteral.length() - 1);
  }

}
