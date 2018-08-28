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

import java.util.List;

public class ApmType {

  public boolean isApmBoolean() {
    return this instanceof ApmBoolean;
  }

  public ApmBoolean getApmBoolean() {
    return (ApmBoolean) this;
  }

  public Boolean getBoolean() {
    return null;
  }

  public boolean isApmNumber() {
    return this instanceof ApmNumber;
  }

  public ApmNumber getApmNumber() {
    return (ApmNumber) this;
  }

  public Number getNumber() {
    return null;
  }

  public boolean isApmString() {
    return this instanceof ApmString;
  }

  public ApmString getApmString() {
    return (ApmString) this;
  }

  public String getString() {
    return null;
  }

  public boolean isApmList() {
    return this instanceof ApmList;
  }

  public ApmList getApmList() {
    return (ApmList) this;
  }

  public List<ApmValue> getList() {
    return null;
  }

  public Object getValue() {
    return null;
  }
}
