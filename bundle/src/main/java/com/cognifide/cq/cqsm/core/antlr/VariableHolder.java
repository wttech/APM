package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.cq.cqsm.core.antlr.type.ApmNull;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class VariableHolder {

  private final Deque<Map<String, ApmType>> contexts = new ArrayDeque<>();

  private VariableHolder() {
  }

  public static VariableHolder empty() {
    VariableHolder variableHolder = new VariableHolder();
    variableHolder.createLocalContext();
    return variableHolder;
  }

  public void put(String name, ApmType value) {
    contexts.peek().put(name, value);
  }

  public ApmType get(String name) {
    ApmType value = new ApmNull();
    for (Map<String, ApmType> context : contexts) {
      if (context.containsKey(name)) {
        value = context.get(name);
      }
    }
    return value;
  }

  public void createLocalContext() {
    contexts.add(new HashMap<>());
  }

  public void removeLocalContext() {
    contexts.pop();
  }
}
