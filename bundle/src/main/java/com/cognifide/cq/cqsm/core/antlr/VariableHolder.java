package com.cognifide.cq.cqsm.core.antlr;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class VariableHolder {

  private final Deque<Map<String, String>> contexts = new ArrayDeque<>();

  private VariableHolder() {
  }

  public static VariableHolder empty() {
    VariableHolder variableHolder = new VariableHolder();
    variableHolder.createLocalContext();
    return variableHolder;
  }

  public void put(String name, String value) {
    contexts.peek().put(name, value);
  }

  public String get(String name) {
    String value = null;
    for (Map<String, String> context : contexts) {
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
