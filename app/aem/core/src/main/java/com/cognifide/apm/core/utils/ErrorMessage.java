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

package com.cognifide.apm.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ErrorMessage implements ResponseMessage {

  private final String message;
  private final List<String> errors;
  private final String type = "error";

  public ErrorMessage(String message, List<String> errors) {
    this.message = message;
    this.errors = errors;
  }

  public static ErrorMessageBuilder errorMessageBuilder(String message) {
    return new ErrorMessageBuilder(message);
  }

  public static ErrorMessage errorMessage(String message) {
    return new ErrorMessageBuilder(message).build();
  }

  public String getMessage() {
    return message;
  }

  public List<String> getErrors() {
    return errors;
  }

  public String getType() {
    return type;
  }

  public static class ErrorMessageBuilder {

    private final String message;
    private final List<String> errors = new ArrayList<>();

    public ErrorMessageBuilder(String message) {
      this.message = message;
    }

    public ErrorMessageBuilder addError(String error) {
      this.errors.add(error);
      return this;
    }

    public ErrorMessageBuilder addErrors(Collection<String> errors) {
      this.errors.addAll(errors);
      return this;
    }

    public ErrorMessage build() {
      return new ErrorMessage(message, errors);
    }
  }
}
