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
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VariableHolder {

  private final Deque<Context> contexts = new ArrayDeque<>();

  public static VariableHolder empty() {
    VariableHolder variableHolder = new VariableHolder();
    variableHolder.createLocalContext();
    return variableHolder;
  }

  public void put(String name, ApmType value) {
    contexts.peek().put(name, value);
  }

  public ApmType get(String name) {
    for (Context context : contexts) {
      if (context.containsKey(name)) {
        return context.get(name);
      }
      if (context.isIsolated()) {
        break;
      }
    }
    return new ApmNull();
  }

  public void createIsolatedLocalContext() {
    contexts.push(new Context(true));
  }

  public void createLocalContext() {
    contexts.push(new Context(false));
  }

  public void removeLocalContext() {
    contexts.pop();
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class Context {

    private final Map<String, ApmType> variables = new HashMap<>();
    private final boolean isolated;

    public boolean containsKey(Object key) {
      return variables.containsKey(key);
    }

    public ApmType get(Object key) {
      return variables.get(key);
    }

    public ApmType put(String key, ApmType value) {
      return variables.put(key, value);
    }

    public boolean isIsolated() {
      return isolated;
    }
  }
}
