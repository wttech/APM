package com.cognifide.cq.cqsm.core.antlr.type;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class ApmList extends ApmType {

  private final List<ApmValue> value;

  public ApmList(List<ApmValue> value) {
    this.value = ImmutableList.copyOf(value);
  }

  @Override
  public List<ApmValue> getList() {
    return value;
  }

}
