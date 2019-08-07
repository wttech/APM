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

import com.cognifide.apm.antlr.ApmType;
import com.cognifide.apm.antlr.argument.Arguments;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class ParameterDescriptor {

  private final Class<? extends ApmType> type;

  abstract Object getArgument(Arguments arguments);

  abstract boolean handles(Arguments arguments);

  protected boolean sameType(ApmType apmType) {
    return this.type.equals(apmType.getClass());
  }

  @Getter
  public static class RequiredParameterDescriptor extends ParameterDescriptor {

    private final int index;

    public RequiredParameterDescriptor(Class<? extends ApmType> type, int index) {
      super(type);
      this.index = index;
    }

    @Override
    Object getArgument(Arguments arguments) {
      return arguments.getRequired().get(index).getArgument();
    }

    @Override
    boolean handles(Arguments arguments) {
      return arguments.getRequired().size() > index && sameType(arguments.getRequired().get(index));
    }
  }

  @Getter
  public static class NamedParameterDescriptor extends ParameterDescriptor {

    private final String name;

    public NamedParameterDescriptor(Class<? extends ApmType> type, String name) {
      super(type);
      this.name = name;
    }

    @Override
    Object getArgument(Arguments arguments) {
      return arguments.getNamed().containsKey(name) ? arguments.getNamed().get(name).getArgument() : null;
    }

    @Override
    boolean handles(Arguments arguments) {
      return !arguments.getNamed().containsKey(name) || sameType(arguments.getNamed().get(name));
    }
  }

  @Getter
  public static class FlagsParameterDescriptor extends ParameterDescriptor {

    public FlagsParameterDescriptor(Class<? extends ApmType> type) {
      super(type);
    }

    @Override
    Object getArgument(Arguments arguments) {
      return arguments.getFlags();
    }

    @Override
    boolean handles(Arguments arguments) {
      return true;
    }
  }
}
