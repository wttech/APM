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

package com.cognifide.apm.core.endpoints.response;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class ResponseEntity {

  private final int statusCode;

  private final Map<String, Object> body;

  private ResponseEntity(int statusCode, String message) {
    this.statusCode = statusCode;
    this.body = new HashMap<>();
    addEntry("message", message);
  }

  public ResponseEntity addEntry(String key, Object value) {
    body.put(key, value);
    return this;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Map<String, Object> getBody() {
    return body;
  }

  public static ResponseEntity badRequest(String message) {
    return new ResponseEntity(HttpServletResponse.SC_BAD_REQUEST, message);
  }

  public static ResponseEntity notFound(String message) {
    return new ResponseEntity(HttpServletResponse.SC_NOT_FOUND, message);
  }

  public static ResponseEntity internalServerError(String message) {
    return new ResponseEntity(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
  }

  public static ResponseEntity ok(String message) {
    return new ResponseEntity(HttpServletResponse.SC_OK, message);
  }
}
