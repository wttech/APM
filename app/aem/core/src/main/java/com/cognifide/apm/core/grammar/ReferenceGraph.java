/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.grammar;

import com.cognifide.apm.api.scripts.Script;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class ReferenceGraph {

  private final List<TreeNode> nodes;

  private final List<Transition> transitions;

  public ReferenceGraph() {
    this.nodes = new ArrayList<>();
    this.transitions = new ArrayList<>();
  }

  public TreeNode addNode(Script script) {
    TreeNode node = TreeNodeFactory.create(script);
    nodes.add(node);
    return node;
  }

  public TreeNode getNode(Script script) {
    return nodes.stream()
        .filter(node -> StringUtils.equals(node.getScriptPath(), script.getPath()))
        .findFirst()
        .orElse(null);
  }

  public void createTransition(TreeNode fromNode, Script toScript, TransitionType transitionType) {
    TreeNode toNode = Optional.ofNullable(getNode(toScript))
        .orElse(addNode(toScript));
    transitions.add(new Transition(fromNode, toNode, transitionType));
  }

  public Set<TreeNode> getSubTreeForScript(Script script) {
    TreeNode currentNode = getNode(script);
    Set<TreeNode> subtree = new HashSet<>();
    if (currentNode != null) {
      subtree.add(currentNode);
      transitions.stream()
          .filter(transition -> Objects.equals(transition.from, currentNode))
          .forEach(transition -> subtree.addAll(getSubTreeForScript(transition.to.script)));
    }
    return subtree;
  }

  public Transition getCycleIfExist() {
    return transitions.stream()
        .filter(Transition::isCycleDetected)
        .findFirst()
        .orElse(null);
  }

  public void detectCycle(TreeNode fromNode, Script toScript) {
    transitions.stream()
        .filter(transition -> Objects.equals(transition.from, fromNode) && Objects.equals(transition.to, getNode(toScript)))
        .findFirst()
        .ifPresent(it -> it.setCycleDetected(true));
  }

  public static class Transition {

    private final TreeNode from;

    private final TreeNode to;

    private final TransitionType transitionType;

    private final String id;

    private boolean cycleDetected;

    private Transition(TreeNode from, TreeNode to, TransitionType transitionType) {
      this.from = from;
      this.to = to;
      this.transitionType = transitionType;
      this.id = String.format("%s|%s|%s", from.id, to.id, transitionType.name());
    }

    public TreeNode getFrom() {
      return from;
    }

    public TreeNode getTo() {
      return to;
    }

    public boolean isCycleDetected() {
      return cycleDetected;
    }

    public void setCycleDetected(boolean cycleDetected) {
      this.cycleDetected = cycleDetected;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof Transition) {
        Transition that = (Transition) obj;
        return Objects.equals(id, that.id);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }
  }

  public enum TransitionType {
    IMPORT, RUN_SCRIPT
  }

  public static class TreeNode {

    private final Script script;

    private final String id;

    private boolean visited;

    private boolean valid;

    private TreeNode(Script script) {
      this(script, true);
    }

    private TreeNode(Script script, boolean valid) {
      this.script = script;
      this.id = DigestUtils.md5Hex(script.getPath());
      this.valid = valid;
    }

    public String getScriptPath() {
      return script.getPath();
    }

    public Script getScript() {
      return script;
    }

    public boolean isVisited() {
      return visited;
    }

    public void setVisited(boolean visited) {
      this.visited = visited;
    }

    public void setValid(boolean valid) {
      this.valid = valid;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof TreeNode) {
        TreeNode that = (TreeNode) obj;
        return Objects.equals(id, that.id);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }
  }

  public static class NonExistingTreeNode extends TreeNode {

    public NonExistingTreeNode(Script script) {
      super(script, false);
    }
  }

  private static class TreeNodeFactory {

    public static TreeNode create(Script script) {
      if (script instanceof ReferenceFinder.NonExistingScript) {
        return new ReferenceGraph.NonExistingTreeNode(script);
      }
      return new ReferenceGraph.TreeNode(script);
    }
  }
}
