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

package com.cognifide.cq.cqsm.core.antlr.type;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;

public class ApmList extends ApmType {

  private final List<ApmValue> value;

  public ApmList(List<ApmValue> value) {
    this.value = ImmutableList.copyOf(value);
  }

  @Override
  public List<ApmValue> getList() {
    return value;
  }

  @Override
  public List<Object> getValue() {
    return value.stream().map(ApmType::getValue).collect(Collectors.toList());
  }
}
