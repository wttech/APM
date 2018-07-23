package com.cognifide.cq.cqsm.core.antlr.type;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;

public class ApmList extends ApmType {

  private final List<ApmValue> value;

  public ApmList(List<ApmValue> value) {
    this.value = ImmutableList.copyOf(value);
  }

  @Override
  public List<ApmValue> getList() {
    return value;
  }

  @Override
  public List<Object> getValue() {
    return value.stream().map(ApmType::getValue).collect(Collectors.toList());
  }
}
