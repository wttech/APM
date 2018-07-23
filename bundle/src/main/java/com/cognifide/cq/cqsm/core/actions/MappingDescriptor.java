package com.cognifide.cq.cqsm.core.actions;

import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class MappingDescriptor implements Comparable<MappingDescriptor> {

  private final Mapping annotation;
  private final Method method;

  @Override
  public int compareTo(MappingDescriptor o) {
    return Integer.compare(this.annotation.order(), o.annotation.order());
  }
}
