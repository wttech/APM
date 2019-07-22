/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.cq.cqsm.core.actions;

import com.cognifide.apm.antlr.ApmInteger;
import com.cognifide.apm.antlr.ApmList;
import com.cognifide.apm.antlr.ApmString;
import com.cognifide.apm.antlr.ApmType;
import com.cognifide.cq.cqsm.api.actions.annotations.Flags;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.actions.annotations.Named;
import com.cognifide.cq.cqsm.core.actions.ParameterDescriptor.FlagsParameterDescriptor;
import com.cognifide.cq.cqsm.core.actions.ParameterDescriptor.NamedParameterDescriptor;
import com.cognifide.cq.cqsm.core.actions.ParameterDescriptor.RequiredParameterDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class MapperDescriptorFactory {

  public MapperDescriptor create(Class<?> mapperClass) {
    Mapper mapperAnnotation = mapperClass.getDeclaredAnnotation(Mapper.class);
    Preconditions.checkArgument(mapperAnnotation != null, "Mapper must be annotated with " + Mapper.class.getName());

    final Object mapper = createInstance(mapperClass);
    final String name = mapperAnnotation.value();
    final List<MappingDescriptor> mappingDescriptors = Lists.newArrayList();
    for (Method method : mapperClass.getDeclaredMethods()) {
      create(method).ifPresent(mappingDescriptors::add);
    }
    return new MapperDescriptor(mapper, name, ImmutableList.copyOf(mappingDescriptors));
  }

  private Object createInstance(Class<?> mapperClass) {
    try {
      return mapperClass.newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException("Cannot instantiate action mapper", e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Cannot construct action mapper, is constructor non public?", e);
    }
  }

  private Optional<MappingDescriptor> create(Method method) {
    Mapping mappingAnnotation = method.getAnnotation(Mapping.class);
    if (mappingAnnotation == null) {
      return Optional.empty();
    }

    List<ParameterDescriptor> parameterDescriptors = Lists.newArrayList();
    AnnotatedType[] annotatedParameterTypes = method.getAnnotatedParameterTypes();
    for (int i = 0; i < annotatedParameterTypes.length; i++) {
      AnnotatedType annotatedParameterType = annotatedParameterTypes[i];
      Class<? extends ApmType> apmType = getApmType(annotatedParameterType.getType());
      ParameterDescriptor parameterDescriptor;
      if (annotatedParameterType.isAnnotationPresent(Named.class)) {
        Named namedAnnotation = annotatedParameterType.getDeclaredAnnotation(Named.class);
        parameterDescriptor = new NamedParameterDescriptor(apmType, namedAnnotation.value());
      } else if (annotatedParameterType.isAnnotationPresent(Flags.class)) {
        parameterDescriptor = new FlagsParameterDescriptor(apmType);
      } else {
        parameterDescriptor = new RequiredParameterDescriptor(apmType);
      }
      parameterDescriptors.add(parameterDescriptor);
    }

    return Optional.of(new MappingDescriptor(method, ImmutableList.copyOf(parameterDescriptors)));
  }

  private Class<? extends ApmType> getApmType(Type type) {
    if (type instanceof Class) {
      Class aClass = (Class) type;
      if (String.class.equals(aClass)) {
        return ApmString.class;
      }
      if (Integer.class.equals(aClass)) {
        return ApmInteger.class;
      }
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Class rawType = (Class) parameterizedType.getRawType();
      if (List.class.equals(rawType)) {
        return ApmList.class;
      }
    }
    return null;
  }
}
