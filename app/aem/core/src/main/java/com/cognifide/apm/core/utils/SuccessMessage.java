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

package com.cognifide.apm.core.utils;

import java.util.HashMap;
import java.util.Map;

public class SuccessMessage extends HashMap<String, Object> implements ResponseMessage {

  public static final String SUCCESS = "success";
  public static final String MESSAGE = "message";
  public static final String TYPE = "type";

  private SuccessMessage(String message) {
    put(MESSAGE, message);
    put(TYPE, SUCCESS);
  }

  private SuccessMessage(String message, Map<String, Object> properties) {
    putAll(properties);
    put(MESSAGE, message);
    put(TYPE, SUCCESS);
  }

  public static SuccessMessage successMessage(String message) {
    return new SuccessMessage(message);
  }

  public static SuccessMessageBuilder successMessageBuilder(String message) {
    return new SuccessMessageBuilder(message);
  }

  @Override
  public String getType() {
    return SUCCESS;
  }

  public static class SuccessMessageBuilder {

    private final String message;
    private final Map<String, Object> properties = new HashMap<>();

    SuccessMessageBuilder(String message) {
      this.message = message;
    }

    public SuccessMessageBuilder addProperty(String key, Object value) {
      this.properties.put(key, value);
      return this;
    }

    public SuccessMessage build() {
      return new SuccessMessage(message, properties);
    }
  }
}
