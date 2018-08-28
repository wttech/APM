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

package com.cognifide.cq.cqsm.core.loader;

import com.cognifide.cq.cqsm.api.scripts.Script;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;

public final class ScriptTree {

  @Getter
  private final Script root;
  private final Map<String, Script> includedScripts;

  public ScriptTree(Script root, Map<String, Script> includedScripts) {
    this.root = root;
    this.includedScripts = ImmutableMap.copyOf(includedScripts);
  }

  public Script getIncludedScript(String path) {
    return includedScripts.get(path);
  }

  public Collection<Script> getIncludedScripts() {
    return includedScripts.values();
  }
}
