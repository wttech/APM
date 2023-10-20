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
import java.util.stream.Collectors;
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
        if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() instanceof Class<?> && Map.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType())) {
          return extractParams((SlingHttpServletRequest) adaptable, parameterName);
        } else if (type instanceof Class<?>) {
          return getValue((SlingHttpServletRequest) adaptable, (Class<?>) type, parameterName, annotatedElement);
        }
      }
    }
    return null;
  }

  private Object getValue(SlingHttpServletRequest request, Class<?> type, String parameterName, AnnotatedElement annotatedElement) {
    org.apache.sling.api.request.RequestParameter parameterValue = request.getRequestParameter(parameterName);
    if (parameterValue == null) {
      return null;
    } else if (annotatedElement.isAnnotationPresent(FileName.class)) {
      return parameterValue.getFileName();
    } else if (type == Integer.class || type == int.class) {
      return Integer.parseInt(parameterValue.getString());
    } else if (type == Boolean.class || type == boolean.class) {
      return BooleanUtils.toBoolean(parameterValue.getString());
    } else if (type == InputStream.class) {
      return toInputStream(parameterValue);
    } else if (type == LocalDateTime.class) {
      return toLocalDateTime(annotatedElement, parameterValue);
    } else if (type.isEnum()) {
      return toEnum(type, parameterValue);
    } else if (type == String[].class) {
      return parameterValue.getString().split(",");
    }
    return parameterValue.getString();
  }

  private InputStream toInputStream(org.apache.sling.api.request.RequestParameter parameterValue) {
    try {
      return parameterValue.getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Object toEnum(Class<?> type, org.apache.sling.api.request.RequestParameter parameterValue) {
    return Arrays.stream(type.getEnumConstants())
        .filter(item -> StringUtils.equals(item.toString(), parameterValue.getString()))
        .findFirst()
        .orElse(null);
  }

  private Map<String, String> extractParams(SlingHttpServletRequest request, String prefix) {
    return request.getParameterMap()
        .entrySet()
        .stream()
        .filter(entry -> StringUtils.startsWith(entry.getKey(), prefix))
        .collect(Collectors.toMap(
            entry -> StringUtils.uncapitalize(StringUtils.removeStart(entry.getKey(), prefix)),
            entry -> entry.getValue()[0]
        ));
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
