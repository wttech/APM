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

package com.cognifide.cq.cqsm.core.loader;

import com.cognifide.apm.antlr.ApmLangParser;
import com.cognifide.apm.antlr.ApmLangParser.ApmContext;
import com.cognifide.apm.antlr.ApmLangParser.ScriptInclusionContext;
import com.cognifide.cq.cqsm.api.exceptions.ExecutionException;
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
    script.setApm(root);
    Map<String, Script> includedScripts = findIncludedScripts(finder, root);
    return new ScriptTree(script, includedScripts);
  }

  private Map<String, Script> findIncludedScripts(ScriptInclusionFinder finder, ApmContext root) {
    Map<String, Script> references = new HashMap<>();
    List<ScriptInclusionContext> scriptInclusions = finder.visit(root);
    for (ScriptInclusionContext scriptInclusion : scriptInclusions) {
      String referencePath = ScriptInclusion.of(scriptInclusion).getPath();
      Script includedScript = findIncludedScript(referencePath);
      references.put(referencePath, includedScript);
    }
    return references;
  }

  private Script findIncludedScript(String referencePath) {
    if (referencePath != null) {
      Script script = scriptFinder.find(referencePath, resourceResolver);
      if (script != null) {
        ApmLangParser includedScript = ApmLangParserFactory.createParserForScript(script.getData());
        script.setApm(includedScript.apm());
        return script;
      }
    }
    throw new ExecutionException("Cannot find script: " + referencePath);
  }

  private static class ScriptInclusionFinder extends ListBaseVisitor<ScriptInclusionContext> {

    @Override
    public List<ScriptInclusionContext> visitScriptInclusion(ScriptInclusionContext ctx) {
      return Collections.singletonList(ctx);
    }
  }
}
