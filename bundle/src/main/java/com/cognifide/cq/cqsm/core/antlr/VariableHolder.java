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

package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.cq.cqsm.core.antlr.type.ApmNull;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VariableHolder {

  private final Deque<Map<String, ApmType>> contexts = new ArrayDeque<>();

  public static VariableHolder empty() {
    VariableHolder variableHolder = new VariableHolder();
    variableHolder.createLocalContext();
    return variableHolder;
  }

  public void put(String name, ApmType value) {
    contexts.peek().put(name, value);
  }

  public ApmType get(String name) {
    return contexts.stream()
        .filter(context -> context.containsKey(name))
        .findFirst()
        .map(context -> context.get(name))
        .orElse(new ApmNull());
  }

  public void createLocalContext() {
    contexts.push(new HashMap<>());
  }

  public void removeLocalContext() {
    contexts.pop();
  }
}
