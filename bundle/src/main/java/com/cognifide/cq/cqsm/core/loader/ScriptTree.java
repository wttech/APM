package com.cognifide.cq.cqsm.core.loader;

import com.cognifide.apm.antlr.ApmLangParser.ApmContext;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;

public class ScriptTree {

  private final ApmContext root;
  private final Map<String, ApmContext> includedScripts;

  public ScriptTree(ApmContext root, Map<String, ApmContext> includedScripts) {
    this.root = root;
    this.includedScripts = ImmutableMap.copyOf(includedScripts);
  }

  public ApmContext getRoot() {
    return root;
  }

  public ApmContext getIncludedScript(String path) {
    return includedScripts.get(path);
  }

  public Collection<ApmContext> getIncludedScripts() {
    return includedScripts.values();
  }
}
