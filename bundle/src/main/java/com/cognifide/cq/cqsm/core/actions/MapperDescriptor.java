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
import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MapperDescriptor {

  private final Object mapper;
  private final String name;
  private final List<MappingDescriptor> mappingDescriptors;

  public boolean handles(Arguments arguments) {
    return mappingDescriptors.stream().anyMatch(it -> it.handles(arguments));
  }

  public ActionDescriptor handle(Arguments arguments) {
    return mappingDescriptors.stream()
        .filter(it -> it.handles(arguments)).findFirst()
        .orElseThrow(() -> new RuntimeException(""))
        .handle(arguments);
  }
}
