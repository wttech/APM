package com.cognifide.cq.cqsm.core.antlr.parameter;

import com.cognifide.cq.cqsm.core.antlr.type.ApmNull;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import com.cognifide.cq.cqsm.core.antlr.type.ApmValue;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;

public class Parameters implements Iterable<ApmType> {

  private final List<ApmType> parameters;

  public Parameters(List<ApmType> parameters) {
    this.parameters = ImmutableList.copyOf(parameters);
  }


  public Boolean getBoolean(int i) {
    return get(i).getBoolean();
  }

  public Boolean getBoolean(int i, Boolean defaultValue) {
    return defaultValue(getBoolean(i), defaultValue);
  }

  public Number getNumber(int i) {
    return get(i).getNumber();
  }

  public Number getNumber(int i, Number defaultValue) {
    return defaultValue(getNumber(i), defaultValue);
  }

  public String getString(int i) {
    return get(i).getString();
  }

  public String getString(int i, String defaultValue) {
    return defaultValue(getString(i), defaultValue);
  }

  public List<ApmValue> getList(int i) {
    return get(i).getList();
  }

  public List<ApmValue> getList(int i, List<ApmValue> defaultValue) {
    return defaultValue(getList(i), defaultValue);
  }

  private <T> T defaultValue(T value, T defaultValue) {
    return value != null ? value : defaultValue;
  }

  public ApmType get(int i) {
    if (i >= 0 && i < parameters.size()) {
      return parameters.get(i);
    }
    return new ApmNull();
  }

  public int size() {
    return parameters.size();
  }

  @Override
  public Iterator<ApmType> iterator() {
    return parameters.iterator();
  }
}
