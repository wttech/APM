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

package com.cognifide.apm.core.actions;

import static java.util.Collections.singletonList;

import com.cognifide.apm.api.actions.annotations.Flag;
import com.cognifide.apm.api.actions.annotations.Named;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.argument.Arguments;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class ParameterDescriptor {

  private final Class<? extends ApmType> type;

  abstract Object getArgument(Arguments arguments);

  abstract boolean handles(Arguments arguments);

  abstract List<ArgumentDescription> toArgumentDescriptions();

  protected boolean sameType(ApmType apmType) {
    return this.type.equals(apmType.getClass());
  }

  @Getter
  public static class RequiredParameterDescriptor extends ParameterDescriptor {

    private final int index;
    private final List<ArgumentDescription> argumentDescriptions;

    public RequiredParameterDescriptor(Class<? extends ApmType> type, int index, Required required) {
      super(type);
      this.index = index;
      this.argumentDescriptions = singletonList(new ArgumentDescription(
          required != null ? required.value() : "(" + index + ")",
          "required",
          required != null ? required.description() : ""
      ));
    }

    @Override
    Object getArgument(Arguments arguments) {
      return arguments.getRequired().get(index).getArgument();
    }

    @Override
    boolean handles(Arguments arguments) {
      return arguments.getRequired().size() > index && sameType(arguments.getRequired().get(index));
    }

    @Override
    List<ArgumentDescription> toArgumentDescriptions() {
      return argumentDescriptions;
    }
  }

  @Getter
  public static class NamedParameterDescriptor extends ParameterDescriptor {

    private final String name;
    private final List<ArgumentDescription> argumentDescriptions;

    public NamedParameterDescriptor(Class<? extends ApmType> type, Named named) {
      super(type);
      this.name = named.value();
      this.argumentDescriptions = singletonList(new ArgumentDescription(
          name, "named", named.description()
      ));
    }

    @Override
    Object getArgument(Arguments arguments) {
      return arguments.getNamed().containsKey(name) ? arguments.getNamed().get(name).getArgument() : null;
    }

    @Override
    boolean handles(Arguments arguments) {
      return !arguments.getNamed().containsKey(name) || sameType(arguments.getNamed().get(name));
    }

    @Override
    List<ArgumentDescription> toArgumentDescriptions() {
      return argumentDescriptions;
    }
  }

  @Getter
  public static class FlagsParameterDescriptor extends ParameterDescriptor {

    private final List<ArgumentDescription> argumentDescriptions;

    public FlagsParameterDescriptor(Class<? extends ApmType> type, Flag[] flags) {
      super(type);
      this.argumentDescriptions = Arrays.stream(flags)
          .map(flag -> new ArgumentDescription(flag.value(), "flag", flag.description()))
          .collect(Collectors.toList());
    }

    @Override
    Object getArgument(Arguments arguments) {
      return arguments.getFlags();
    }

    @Override
    boolean handles(Arguments arguments) {
      return true;
    }

    @Override
    List<ArgumentDescription> toArgumentDescriptions() {
      return argumentDescriptions;
    }
  }

  @Getter
  public static class FlagParameterDescriptor extends ParameterDescriptor {

    private final List<ArgumentDescription> argumentDescriptions;
    private final String flag;

    public FlagParameterDescriptor(Class<? extends ApmType> type, Flag flag) {
      super(type);
      this.argumentDescriptions = singletonList(new ArgumentDescription(flag.value(), "flag", flag.description()));
      this.flag = flag.value();
    }

    @Override
    Object getArgument(Arguments arguments) {
      return arguments.getFlags().contains(flag);
    }

    @Override
    boolean handles(Arguments arguments) {
      return true;
    }

    @Override
    List<ArgumentDescription> toArgumentDescriptions() {
      return argumentDescriptions;
    }
  }
}
