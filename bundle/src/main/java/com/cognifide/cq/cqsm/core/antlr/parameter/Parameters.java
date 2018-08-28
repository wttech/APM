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

package com.cognifide.cq.cqsm.core.antlr.parameter;

import com.cognifide.cq.cqsm.core.antlr.type.ApmNull;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import com.cognifide.cq.cqsm.core.antlr.type.ApmValue;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;

public class Parameters implements Iterable<ApmType> {

  private final List<ApmType> params;

  public Parameters(List<ApmType> params) {
    this.params = ImmutableList.copyOf(params);
  }


  public Boolean getBoolean(int i) {
    return get(i).getBoolean();
  }

  public Boolean getBoolean(int i, Boolean defaultValue) {
    return defaultValue(getBoolean(i), defaultValue);
  }

  public Number getNumber(int i) {
    return get(i).getNumber();
  }

  public Number getNumber(int i, Number defaultValue) {
    return defaultValue(getNumber(i), defaultValue);
  }

  public String getString(int i) {
    return get(i).getString();
  }

  public String getString(int i, String defaultValue) {
    return defaultValue(getString(i), defaultValue);
  }

  public List<ApmValue> getList(int i) {
    return get(i).getList();
  }

  public List<ApmValue> getList(int i, List<ApmValue> defaultValue) {
    return defaultValue(getList(i), defaultValue);
  }

  private <T> T defaultValue(T value, T defaultValue) {
    return value != null ? value : defaultValue;
  }

  public ApmType get(int i) {
    if (i >= 0 && i < params.size()) {
      return params.get(i);
    }
    return new ApmNull();
  }

  public int size() {
    return params.size();
  }

  @Override
  public Iterator<ApmType> iterator() {
    return params.iterator();
  }
}
