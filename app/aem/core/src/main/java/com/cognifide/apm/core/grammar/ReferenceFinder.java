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

package com.cognifide.apm.core.grammar;

import com.cognifide.apm.api.scripts.LaunchEnvironment;
import com.cognifide.apm.api.scripts.LaunchMode;
import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser;
import com.cognifide.apm.core.grammar.common.Functions;
import com.cognifide.apm.core.grammar.executioncontext.ExecutionContext;
import com.cognifide.apm.core.grammar.parsedscript.ParsedScript;
import com.cognifide.apm.core.progress.ProgressImpl;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.sling.api.resource.ResourceResolver;

public class ReferenceFinder {

  private final ScriptFinder scriptFinder;

  private final ResourceResolver resourceResolver;

  public ReferenceFinder(ScriptFinder scriptFinder, ResourceResolver resourceResolver) {
    this.scriptFinder = scriptFinder;
    this.resourceResolver = resourceResolver;
  }

  public List<Script> findReferences(Script script) {
    Set<Script> result = new TreeSet<>(Comparator.comparing(Script::getPath));
    ReferenceGraph refGraph = getReferenceGraph(script);
    ReferenceGraph.Transition possibleCycle = refGraph.getCycleIfExist();
    if (possibleCycle != null) {
      throw new ScriptExecutionException(String.format("Cycle detected %s -> %s", possibleCycle.getFrom().getScriptPath(), possibleCycle.getTo().getScriptPath()));
    } else {
      refGraph.getSubTreeForScript(script)
          .forEach(node -> {
            if (node instanceof ReferenceGraph.NonExistingTreeNode) {
              throw new ScriptExecutionException(String.format("Script doesn't exist %s", node.getScriptPath()));
            } else {
              result.add(node.getScript());
            }
          });
    }
    return new ArrayList<>(result);
  }

  public ReferenceGraph getReferenceGraph(Script... scripts) {
    ReferenceGraph refGraph = new ReferenceGraph();
    for (Script script : scripts) {
      fillReferenceGraph(refGraph, script);
    }
    return refGraph;
  }

  private void fillReferenceGraph(ReferenceGraph refGraph, Script script) {
    if (refGraph.getNode(script) == null) {
      ApmLangParser.ApmContext apmContext = ParsedScript.create(script).getApm();
      ExecutionContext executionContext = ExecutionContext.create(scriptFinder, resourceResolver, script, new ProgressImpl(resourceResolver.getUserID()));
      findReferences(refGraph, refGraph.addNode(script), ImmutableList.of(script.getPath()), executionContext, apmContext);
    }
  }

  private void findReferences(ReferenceGraph refGraph, ReferenceGraph.TreeNode currentNode, List<String> ancestors, ExecutionContext executionContext, ApmLangParser.ApmContext ctx) {
    InternalVisitor internalVisitor = new InternalVisitor(executionContext, refGraph, currentNode);
    internalVisitor.visitApm(ctx);
    currentNode.setVisited(true);
    internalVisitor.scripts
        .forEach(script -> {
          ReferenceGraph.TreeNode node = refGraph.getNode(script);
          if (node != null && !(node instanceof ReferenceGraph.NonExistingTreeNode)) {
            ParsedScript parsedScript = executionContext.loadScript(script.getPath());
            executionContext.createScriptContext(parsedScript);
            if (ancestors.contains(script.getPath())) {
              refGraph.detectCycle(currentNode, script);
            } else if (!node.isVisited()) {
              List<String> newAncestors = new ArrayList<>(ancestors);
              newAncestors.add(script.getPath());
              findReferences(refGraph, node, newAncestors, executionContext, parsedScript.getApm());
            }
          }
        });
  }

  private static class InternalVisitor extends ApmLangBaseVisitor<Object> {

    private final ExecutionContext executionContext;

    private final ReferenceGraph refGraph;

    private final ReferenceGraph.TreeNode currentNode;

    private final Set<Script> scripts;

    public InternalVisitor(ExecutionContext executionContext, ReferenceGraph refGraph, ReferenceGraph.TreeNode currentNode) {
      this.executionContext = executionContext;
      this.refGraph = refGraph;
      this.currentNode = currentNode;
      this.scripts = new HashSet<>();
    }

    @Override
    public Object visitImportScript(ApmLangParser.ImportScriptContext ctx) {
      String foundPath = Functions.getPath(ctx.path());
      createTransition(foundPath, ReferenceGraph.TransitionType.IMPORT);
      return null;
    }

    @Override
    public Object visitRunScript(ApmLangParser.RunScriptContext ctx) {
      String foundPath = Functions.getPath(ctx.path());
      createTransition(foundPath, ReferenceGraph.TransitionType.RUN_SCRIPT);
      return null;
    }

    private void createTransition(String foundPath, ReferenceGraph.TransitionType transitionType) {
      Script script = loadScript(foundPath);
      if (script != null) {
        refGraph.createTransition(currentNode, script, transitionType);
        scripts.add(script);
      }
    }

    private Script loadScript(String foundPath) {
      Script script = null;
      if (foundPath != null) {
        try {
          script = executionContext.loadScript(foundPath).getScript();
        } catch (ScriptExecutionException e) {
          script = new NonExistingScript(foundPath);
          currentNode.setValid(false);
        }
      }
      return script;
    }
  }

  public static class NonExistingScript implements Script {

    private final String scriptPath;

    public NonExistingScript(String scriptPath) {
      this.scriptPath = scriptPath;
    }

    @Override
    public String getPath() {
      return scriptPath;
    }

    @Override
    public boolean isValid() {
      return false;
    }

    @Override
    public boolean isLaunchEnabled() {
      return false;
    }

    @Override
    public LaunchMode getLaunchMode() {
      return null;
    }

    @Override
    public LaunchEnvironment getLaunchEnvironment() {
      return null;
    }

    @Override
    public Set<String> getLaunchRunModes() {
      return null;
    }

    @Override
    public String getLaunchHook() {
      return null;
    }

    @Override
    public Date getLaunchSchedule() {
      return null;
    }

    @Override
    public String getLaunchCronExpression() {
      return null;
    }

    @Override
    public Date getLastExecuted() {
      return null;
    }

    @Override
    public String getChecksum() {
      return null;
    }

    @Override
    public String getAuthor() {
      return null;
    }

    @Override
    public Date getLastModified() {
      return null;
    }

    @Override
    public String getData() {
      return null;
    }
  }
}
