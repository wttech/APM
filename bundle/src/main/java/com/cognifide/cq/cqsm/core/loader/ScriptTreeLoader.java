package com.cognifide.cq.cqsm.core.loader;

import com.cognifide.apm.antlr.ApmLangParser;
import com.cognifide.apm.antlr.ApmLangParser.ApmContext;
import com.cognifide.apm.antlr.ApmLangParser.ScriptInclusionContext;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.core.antlr.ApmLangParserFactory;
import com.cognifide.cq.cqsm.core.antlr.ListBaseVisitor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;

public class ScriptTreeLoader {

  private final ResourceResolver resourceResolver;
  private final ScriptFinder scriptFinder;

  public ScriptTreeLoader(ResourceResolver resourceResolver, ScriptFinder scriptFinder) {
    this.resourceResolver = resourceResolver;
    this.scriptFinder = scriptFinder;
  }

  public ScriptTree loadScriptTree(Script script) {
    ScriptInclusionFinder finder = new ScriptInclusionFinder();
    ApmContext root = ApmLangParserFactory.createParserForScript(script.getData()).apm();
    Map<String, ApmContext> includedScripts = findIncludedScripts(finder, root);
    return new ScriptTree(root, includedScripts);
  }

  private Map<String, ApmContext> findIncludedScripts(ScriptInclusionFinder finder, ApmContext root) {
    Map<String, ApmContext> references = new HashMap<>();
    List<ScriptInclusionContext> scriptInclusions = finder.visit(root);
    for (ScriptInclusionContext scriptInclusion : scriptInclusions) {
      String referencePath = ScriptInclusion.of(scriptInclusion).getPath();
      if (referencePath != null) {
        Script script = scriptFinder.find(referencePath, resourceResolver);
        ApmLangParser includedScript = ApmLangParserFactory.createParserForScript(script.getData());
        references.put(referencePath, includedScript.apm());
      }
    }
    return references;
  }

  private static class ScriptInclusionFinder extends ListBaseVisitor<ScriptInclusionContext> {

    @Override
    public List<ScriptInclusionContext> visitScriptInclusion(ScriptInclusionContext ctx) {
      return Collections.singletonList(ctx);
    }
  }
}
