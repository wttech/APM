package com.cognifide.cq.cqsm.core.antlr.type;

public class ApmBoolean extends ApmValue {

  private final Boolean value;

  public ApmBoolean(Boolean value) {
    this.value = value;
  }

  @Override
  public Boolean getBoolean() {
    return value;
  }

  @Override
  public Object getValue() {
    return value;
  }
}
