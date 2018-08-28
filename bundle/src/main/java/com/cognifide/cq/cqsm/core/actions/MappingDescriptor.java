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
