package com.cognifide.cq.cqsm.core.antlr.type;

public class ApmString extends ApmValue {

  private final String value;

  public ApmString(String value) {
    this.value = value;
  }

  @Override
  public String getString() {
    return value;
  }

  @Override
  public String getValue() {
    return value;
  }
}
