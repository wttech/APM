package com.cognifide.cq.cqsm.core.loader;

import com.cognifide.apm.antlr.ApmLangParser.ScriptInclusionContext;
import com.cognifide.cq.cqsm.core.antlr.StringLiteral;

public final class ScriptInclusion {

  private final String path;
  private final ScriptInclusionContext inclusion;

  public ScriptInclusion(String path, ScriptInclusionContext inclusion) {
    this.path = path;
    this.inclusion = inclusion;
  }

  public static ScriptInclusion of(ScriptInclusionContext inclusion) {
    String path = StringLiteral.getValue(inclusion.path().STRING_LITERAL());
    return new ScriptInclusion(path, inclusion);
  }

  public String getPath() {
    return path;
  }

  public ScriptInclusionContext getInclusion() {
    return inclusion;
  }
}
