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

package com.cognifide.apm.core.endpoints.params;

import com.cognifide.apm.core.Property;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(
    property = {
        Constants.SERVICE_RANKING + "=" + Integer.MIN_VALUE,
        Property.VENDOR
    }
)
public class RequestParameterInjector implements Injector, StaticInjectAnnotationProcessorFactory {

  @Override
  public String getName() {
    return "apm-request-parameter";
  }

  @Override
  public Object getValue(Object adaptable, String fieldName, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
    if (adaptable instanceof SlingHttpServletRequest) {
      RequestParameter annotation = annotatedElement.getAnnotation(RequestParameter.class);
      if (annotation != null) {
        String parameterName = annotation.value();
        if (type instanceof ParameterizedType
            && ((ParameterizedType) type).getRawType() instanceof Class<?>
            && Map.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType())) {
          return extractParams((SlingHttpServletRequest) adaptable, fieldName);
        } else if (type instanceof Class<?>) {
          return getValue((SlingHttpServletRequest) adaptable, (Class<?>) type, StringUtils.defaultString(parameterName, fieldName), annotatedElement);
        }
      }
    }
    return null;
  }

  private Object getValue(SlingHttpServletRequest request, Class<?> fieldClass, String fieldName, AnnotatedElement annotatedElement) {
    org.apache.sling.api.request.RequestParameter parameterValue = request.getRequestParameter(fieldName);
    Object result;
    if (parameterValue == null) {
      result = null;
    } else if (annotatedElement.isAnnotationPresent(FileName.class)) {
      result = parameterValue.getFileName();
    } else if (StringUtils.equalsAny(fieldClass.getName(), "java.lang.Integer", "int")) {
      result = toInt(parameterValue);
    } else if (StringUtils.equalsAny(fieldClass.getName(), "java.lang.Boolean", "boolean")) {
      result = BooleanUtils.toBoolean(parameterValue.getString());
    } else if (fieldClass == InputStream.class) {
      result = toInputStream(parameterValue);
    } else if (fieldClass == LocalDateTime.class) {
      result = toLocalDateTime(annotatedElement, parameterValue);
    } else if (Enum.class.isAssignableFrom(fieldClass)) {
      result = toEnum(fieldClass, parameterValue);
    } else if (fieldClass.getCanonicalName().equals("java.lang.String[]")) {
      result = parameterValue.getString().split(",");
    } else {
      result = parameterValue.getString();
    }
    return result;
  }

  private Object toInt(org.apache.sling.api.request.RequestParameter parameterValue) {
    Object result = null;
    try {
      result = Integer.parseInt(parameterValue.getString());
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return result;
  }

  private Object toInputStream(org.apache.sling.api.request.RequestParameter parameterValue) {
    Object result = null;
    try {
      result = parameterValue.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private Object toEnum(Class<?> type, org.apache.sling.api.request.RequestParameter parameterValue) {
    return Optional.ofNullable(type.getEnumConstants())
        .map(Arrays::stream)
        .orElse(Stream.empty())
        .filter(x -> StringUtils.equals(x.toString(), parameterValue.getString()))
        .findFirst()
        .orElse(null);
  }

  private Object extractParams(SlingHttpServletRequest request, String prefix) {
//            request.parameterMap.mapValues { (it.value as Array<*>)[0] }
//                    .mapValues { it.value as String }
//                    .mapKeys { it.key as String }
//                    .filterKeys { it.startsWith(prefix) }
//                    .mapKeys { it.key.removePrefix(prefix) }
//                    .mapKeys { it.key.decapitalize() }
    return null;
  }

  private LocalDateTime toLocalDateTime(AnnotatedElement annotatedElement, org.apache.sling.api.request.RequestParameter parameterValue) {
    String dateFormat = Optional.ofNullable(annotatedElement.getAnnotation(DateFormat.class))
        .map(DateFormat::value)
        .orElse(DateTimeFormatter.ISO_LOCAL_DATE_TIME.toString());
    return LocalDateTime.parse(parameterValue.getString(), DateTimeFormatter.ofPattern(dateFormat));
  }

  @Override
  public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement element) {
    return Optional.ofNullable(element.getAnnotation(RequestParameter.class))
        .map(RequestParameterAnnotationProcessor::new)
        .orElse(null);
  }

  private static class RequestParameterAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

    private final RequestParameter annotation;

    public RequestParameterAnnotationProcessor(RequestParameter annotation) {
      this.annotation = annotation;
    }

    @Override
    public String getName() {
      return annotation.value();
    }

    @Override
    public Boolean isOptional() {
      return annotation.optional();
    }
  }
}
