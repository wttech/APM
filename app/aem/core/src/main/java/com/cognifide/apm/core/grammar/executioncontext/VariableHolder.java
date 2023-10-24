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

package com.cognifide.apm.core.grammar.executioncontext;

import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.ApmType.ApmList;
import com.cognifide.apm.core.grammar.ApmType.ApmMap;
import com.cognifide.apm.core.grammar.argument.ArgumentResolverException;
import com.cognifide.apm.core.grammar.common.Functions;
import com.cognifide.apm.core.grammar.common.StackWithRoot;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;

public class VariableHolder {

  private final StackWithRoot<Context> contexts;

  public VariableHolder() {
    this.contexts = new StackWithRoot<>(new Context());
    createLocalContext();
  }

  public Authorizable getAuthorizable() {
    return StreamSupport.stream(contexts.spliterator(), false)
        .map(Context::getAuthorizable)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  public void setAuthorizable(Authorizable authorizable) {
    contexts.peek().setAuthorizable(authorizable);
  }

  public void set(String name, ApmType value) {
    contexts.peek().set(name, value);
  }

  public void setAll(VariableHolder variableHolder) {
    contexts.peek().toMap().putAll(variableHolder.contexts.peek().toMap());
  }

  public ApmType get(String name) {
    List<String> keys = Arrays.stream(StringUtils.split(name, "[.]"))
        .filter(StringUtils::isNotEmpty)
        .map(Functions::toPlainString)
        .collect(Collectors.toList());
    Context context = StreamSupport.stream(contexts.spliterator(), false)
        .filter(it -> it.containsKey(keys.get(0)))
        .findFirst()
        .orElse(null);
    ApmType result = null;
    if (context != null) {
      for (String key : keys) {
        if (result instanceof ApmList) {
          try {
            result = result.getList().get(Integer.parseInt(key));
          } catch (NumberFormatException | IndexOutOfBoundsException e) {
            result = null;
          }
        } else if (result instanceof ApmMap) {
          result = result.getMap().get(key);
        } else {
          result = context.get(key);
        }
        if (result == null) {
          break;
        }
      }
    }
    return Optional.ofNullable(result)
        .orElseThrow(() -> new ArgumentResolverException(String.format("Variable \"%s\" not found", name)));
  }

  public void createLocalContext() {
    contexts.push(new Context());
  }

  public void removeLocalContext() {
    contexts.pop();
  }

  public Map<String, ApmType> toMap() {
    return contexts.peek().toMap();
  }

  private static class Context {

    private Authorizable authorizable;

    private final Map<String, ApmType> variables;

    public Context() {
      this.variables = new HashMap<>();
    }

    public Authorizable getAuthorizable() {
      return authorizable;
    }

    public void setAuthorizable(Authorizable authorizable) {
      this.authorizable = authorizable;
    }

    public boolean containsKey(String key) {
      return variables.containsKey(key);
    }

    public void set(String key, ApmType value) {
      variables.put(key, value);
    }

    public ApmType get(String key) {
      return variables.get(key);
    }

    public Map<String, ApmType> toMap() {
      return variables;
    }
  }
}
