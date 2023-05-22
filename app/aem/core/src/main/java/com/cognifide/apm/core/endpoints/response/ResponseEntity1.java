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

import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpStatus;

import java.util.Map;

public class ResponseEntity1 {

    private int statusCode;

    private Map<String, Object> body;

    private ResponseEntity1(int statusCode, String message, Map<String, Object> params) {
        this.statusCode = statusCode;
        this.body = ImmutableMap.<String, Object>builder()
                .put("message", message)
                .putAll(params)
                .build();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public static ResponseEntity1 badRequest(String message, Map<String, Object> params) {
        return new ResponseEntity1(HttpStatus.SC_BAD_REQUEST, message, params);
    }

    public static ResponseEntity1 notFound(String message, Map<String, Object> params) {
        return new ResponseEntity1(HttpStatus.SC_NOT_FOUND, message, params);
    }

    public static ResponseEntity1 internalServerError(String message, Map<String, Object> params) {
        return new ResponseEntity1(HttpStatus.SC_INTERNAL_SERVER_ERROR, message, params);
    }

    public static ResponseEntity1 ok(String message, Map<String, Object> params) {
        return new ResponseEntity1(HttpStatus.SC_OK, message, params);
    }
}
