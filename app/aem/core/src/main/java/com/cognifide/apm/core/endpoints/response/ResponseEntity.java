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

package com.cognifide.apm.core.endpoints.response

import javax.servlet.http.HttpServletResponse

class ResponseEntity<T>(val statusCode: Int, val body: T)

fun badRequest(body: ErrorBody.() -> Unit): ResponseEntity<Any> {
    return error(HttpServletResponse.SC_BAD_REQUEST, body)
}

fun notFound(body: ErrorBody.() -> Unit): ResponseEntity<Any> {
    return error(HttpServletResponse.SC_NOT_FOUND, body)
}

fun internalServerError(body: ErrorBody.() -> Unit): ResponseEntity<Any> {
    return error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, body)
}

fun ok(body: SuccessBody.() -> Unit): ResponseEntity<Any> {
    return success(200, body)
}

fun error(statusCode: Int, body: ErrorBody.() -> Unit): ResponseEntity<Any> {
    return ResponseEntity(statusCode, ErrorBody().apply(body))
}

fun success(statusCode: Int, body: SuccessBody.() -> Unit): ResponseEntity<Any> {
    return ResponseEntity(statusCode, SuccessBody().apply(body))
}