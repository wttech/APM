package com.cognifide.cq.cqsm.core.antlr.type;

import java.util.List;

public class ApmType {

  public boolean isApmBoolean() {
    return this instanceof ApmBoolean;
  }

  public ApmBoolean getApmBoolean() {
    return (ApmBoolean) this;
  }

  public Boolean getBoolean() {
    return null;
  }

  public boolean isApmNumber() {
    return this instanceof ApmNumber;
  }

  public ApmNumber getApmNumber() {
    return (ApmNumber) this;
  }

  public Number getNumber() {
    return null;
  }

  public boolean isApmString() {
    return this instanceof ApmString;
  }

  public ApmString getApmString() {
    return (ApmString) this;
  }

  public String getString() {
    return null;
  }

  public boolean isApmList() {
    return this instanceof ApmList;
  }

  public ApmList getApmList() {
    return (ApmList) this;
  }

  public List<ApmValue> getList() {
    return null;
  }
}
