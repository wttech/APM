package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.apm.antlr.ApmLangLexer;
import com.cognifide.apm.antlr.ApmLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public final class ApmLangParserFactory {

  private ApmLangParserFactory() {
  }

  public static ApmLangParser createParserForScript(String scriptContent) {
    CharStream charStream = CharStreams.fromString(scriptContent);
    ApmLangLexer lexer = new ApmLangLexer(charStream);
    CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
    return new ApmLangParser(commonTokenStream);
  }
}
