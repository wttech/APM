/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.crypto

import com.adobe.granite.crypto.CryptoException
import com.adobe.granite.crypto.CryptoSupport
import com.cognifide.apm.core.Property
import org.apache.commons.lang.text.StrSubstitutor
import org.apache.commons.lang3.StringUtils
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

@Component(
    service = [DecryptionService::class],
    property = [
        Property.DESCRIPTION + "APM Service for decryption encrypted values",
        Property.VENDOR
    ]
)
open class DecryptionService {

    @Reference
    @Transient
    private lateinit var cryptoSupport: CryptoSupport

    fun decrypt(text: String): String {
        val tokens = StringUtils.substringsBetween(text, "{", "}")
            .orEmpty()
            .map { it to unprotect("{$it}") }
            .toMap()
        val strSubstitutor = StrSubstitutor(tokens, "{", "}")
        return if (tokens.isEmpty()) text else strSubstitutor.replace(text)
    }

    protected open fun unprotect(text: String): String {
        return try {
            cryptoSupport.unprotect(text)
        } catch (e: CryptoException) {
            throw IllegalArgumentException(String.format("Unable to decrypt '%s', wrong hmac or master key", text), e)
        }
    }

}
