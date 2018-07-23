package com.cognifide.cq.cqsm.core.actions;

import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class MapperDescriptor {

  private final Mapper annotation;
  private final Object object;
  private final List<MappingDescriptor> mappings;

  public String getCommandName() {
    return annotation.value();
  }
}
