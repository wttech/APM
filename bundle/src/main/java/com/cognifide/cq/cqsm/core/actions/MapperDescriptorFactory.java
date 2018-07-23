package com.cognifide.cq.cqsm.core.actions;

import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapperDescriptorFactory {

  public static MapperDescriptor create(Class<?> clazz) {
    Mapper annotation = clazz.getDeclaredAnnotation(Mapper.class);
    if (annotation != null) {
      try {
        Object mapper = clazz.newInstance();
        return new MapperDescriptor(annotation, mapper, getMappings(mapper));
      } catch (InstantiationException e) {
        throw new CannotCreateMapperException("Cannot instantiate action mapper", e);
      } catch (IllegalAccessException e) {
        throw new CannotCreateMapperException("Cannot construct action mapper, is constructor non public?", e);
      }
    }
    throw new CannotCreateMapperException("Cannot find required annotation on given type " + clazz.getName());
  }

  private static List<MappingDescriptor> getMappings(Object mapper) {
    List<MappingDescriptor> result = new ArrayList<>();
    for (Method method : mapper.getClass().getDeclaredMethods()) {
      Mapping annotation = method.getDeclaredAnnotation(Mapping.class);
      if (annotation != null) {
        result.add(new MappingDescriptor(annotation, method));
      }
    }
    result.sort(null);
    return ImmutableList.copyOf(result);
  }
}
