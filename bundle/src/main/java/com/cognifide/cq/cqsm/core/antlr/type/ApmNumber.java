package com.cognifide.cq.cqsm.core.antlr.type;

public class ApmNumber extends ApmValue {

  private final Number value;

  public ApmNumber(Number value) {
    this.value = value;
  }

  @Override
  public Number getNumber() {
    return value;
  }
}
