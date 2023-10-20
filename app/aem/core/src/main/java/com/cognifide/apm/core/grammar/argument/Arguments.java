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

package com.cognifide.apm.core.grammar.argument;

import com.cognifide.apm.core.grammar.ApmType;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Arguments {

  private final List<ApmType> required;

  private final Map<String, ApmType> named;

  private final List<String> flags;

  public Arguments() {
    this(Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
  }

  public Arguments(List<ApmType> required, Map<String, ApmType> named, List<String> flags) {
    this.required = required;
    this.named = named;
    this.flags = flags;
  }

  public List<ApmType> getRequired() {
    return required;
  }

  public Map<String, ApmType> getNamed() {
    return named;
  }

  public List<String> getFlags() {
    return flags;
  }
}
