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

package com.cognifide.apm.core.endpoints.params


import com.cognifide.apm.core.Property
import com.google.common.primitives.Ints
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory
import org.osgi.framework.Constants
import org.osgi.service.component.annotations.Component
import java.io.InputStream
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component(
        immediate = true,
        service = [Injector::class, StaticInjectAnnotationProcessorFactory::class],
        property = [
            Constants.SERVICE_RANKING + "=" + Int.MIN_VALUE,
            Property.VENDOR
        ])
class RequestParameterInjector : Injector, StaticInjectAnnotationProcessorFactory {

    override fun getName(): String {
        return "apm-request-parameter"
    }

    override fun getValue(adaptable: Any, fieldName: String, type: Type, annotatedElement: AnnotatedElement,
                          disposalCallbackRegistry: DisposalCallbackRegistry): Any? {
        if (adaptable is SlingHttpServletRequest) {
            val annotation = annotatedElement.getAnnotation(RequestParameter::class.java)
            if (annotation != null && type is Class<*>) {
                val parameterName = annotation.value
                return getValue(adaptable, type, StringUtils.defaultString(parameterName, fieldName), annotatedElement)
            }
        }
        return null
    }

    private fun getValue(request: SlingHttpServletRequest, fieldClass: Class<*>, fieldName: String, annotatedElement: AnnotatedElement): Any? {
        val parameterValue = request.getRequestParameter(fieldName) ?: return null
        return when {
            annotatedElement.isAnnotationPresent(FileName::class.java) -> parameterValue.fileName
            fieldClass.name in listOf("java.lang.Integer", "int") -> Ints.tryParse(parameterValue.string)
            fieldClass.name in listOf("java.lang.Boolean", "boolean") -> "true" == parameterValue.string
            fieldClass == InputStream::class.java -> parameterValue.inputStream
            fieldClass == LocalDateTime::class.java -> {
                val dateFormat = annotatedElement.getAnnotation(DateFormat::class.java)?.value
                        ?: DateTimeFormatter.ISO_LOCAL_DATE_TIME.toString()
                LocalDateTime.parse(parameterValue.string, DateTimeFormatter.ofPattern(dateFormat))
            }
            Enum::class.java.isAssignableFrom(fieldClass) -> {
                fieldClass.enumConstants.firstOrNull { it.toString() == parameterValue.string }
            }
            fieldClass.canonicalName == "java.lang.String[]" -> parameterValue.string.split(",").toTypedArray()
            else -> parameterValue.string
        }
    }

    override fun createAnnotationProcessor(element: AnnotatedElement): InjectAnnotationProcessor2? {
        return element.getAnnotation(RequestParameter::class.java)?.let { RequestParameterAnnotationProcessor(it) }
    }

    class RequestParameterAnnotationProcessor(private val annotation: RequestParameter) : AbstractInjectAnnotationProcessor2() {

        override fun getName(): String {
            return annotation.value
        }

        override fun isOptional(): Boolean {
            return annotation.optional
        }
    }
}