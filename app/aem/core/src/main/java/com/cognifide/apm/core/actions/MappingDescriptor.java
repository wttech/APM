/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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

package com.cognifide.apm.core.actions;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.core.actions.ParameterDescriptor.NamedParameterDescriptor;
import com.cognifide.apm.core.actions.ParameterDescriptor.RequiredParameterDescriptor;
import com.cognifide.apm.core.grammar.argument.Arguments;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;

public class MappingDescriptor {

  private final String name;

  private final String group;

  private final String description;

  private final List<String> examples;

  private final Method method;
  private final List<ParameterDescriptor> parameterDescriptors;

  public MappingDescriptor(Method method, Mapper mapper, Mapping mapping, List<ParameterDescriptor> parameterDescriptors) {
    this.name = mapper.value();
    this.group = mapper.group();
    this.description = mapping.reference();
    this.examples = ImmutableList.copyOf(mapping.examples());
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
    return ListUtils.removeAll(arguments.getNamed().keySet(), expectedKeys).isEmpty();
  }

  public Action handle(Object mapper, Arguments arguments, MapperContext mapperContext) {
    List<Object> args = parameterDescriptors.stream()
        .map(parameterDescriptor -> parameterDescriptor.getArgument(arguments, mapperContext.getDecryptionService()))
        .collect(Collectors.toList());
    try {
      return (Action) method.invoke(mapper, args.toArray());
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Cannot invoke mapping method", e);
    }
  }

  public String getName() {
    return name;
  }

  public String getGroup() {
    return group;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getExamples() {
    return examples;
  }
}
