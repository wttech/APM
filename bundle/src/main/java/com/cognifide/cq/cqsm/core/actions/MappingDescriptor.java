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

import com.cognifide.apm.antlr.argument.Arguments;
import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.core.actions.ParameterDescriptor.NamedParameterDescriptor;
import com.cognifide.cq.cqsm.core.actions.ParameterDescriptor.RequiredParameterDescriptor;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

public class MappingDescriptor {

  @Getter
  private final String name;
  @Getter
  private final String description;
  @Getter
  private final List<String> examples;

  private final Method method;
  private final List<ParameterDescriptor> parameterDescriptors;

  public MappingDescriptor(Method method, Mapping mappingAnnotation, List<ParameterDescriptor> parameterDescriptors) {
    this.name = mappingAnnotation.value();
    this.description = mappingAnnotation.reference();
    this.examples = ImmutableList.copyOf(mappingAnnotation.examples());
    this.method = method;
    this.parameterDescriptors = parameterDescriptors;
  }

  public boolean handles(Arguments arguments) {
    boolean handles = parameterDescriptors.stream()
        .allMatch(parameterDescriptor -> parameterDescriptor.handles(arguments));
    handles &= checkRequired(arguments);
    handles &= checkNamed(arguments);
    return handles;
  }

  public List<ArgumentDescription> getArguments() {
    return parameterDescriptors.stream()
        .flatMap(parameterDescriptor -> parameterDescriptor.toArgumentDescriptions().stream())
        .collect(Collectors.toList());
  }

  private boolean checkRequired(Arguments arguments) {
    long expectedCount = parameterDescriptors.stream().filter(RequiredParameterDescriptor.class::isInstance).count();
    return expectedCount == arguments.getRequired().size();
  }

  private boolean checkNamed(Arguments arguments) {
    Set<String> expectedKeys = parameterDescriptors.stream()
        .filter(NamedParameterDescriptor.class::isInstance)
        .map(NamedParameterDescriptor.class::cast)
        .map(NamedParameterDescriptor::getName)
        .collect(Collectors.toSet());
    return CollectionUtils.removeAll(arguments.getNamed().keySet(), expectedKeys).isEmpty();
  }

  public Action handle(Object mapper, Arguments arguments) {
    List<Object> args = parameterDescriptors.stream()
        .map(parameterDescriptor -> parameterDescriptor.getArgument(arguments))
        .collect(Collectors.toList());
    try {
      return (Action) method.invoke(mapper, args.toArray());
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Cannot invoke mapping method", e);
    }
  }
}
