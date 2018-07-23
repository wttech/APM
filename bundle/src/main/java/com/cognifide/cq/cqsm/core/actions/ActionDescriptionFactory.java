/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.cq.cqsm.core.actions;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters;
import com.cognifide.cq.cqsm.core.antlr.type.ApmType;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ActionDescriptionFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ActionDescriptionFactory.class);

  public ActionDescriptor evaluate(MapperDescriptor mapper, String command, Parameters parameters)
      throws ActionCreationException {
    Preconditions.checkArgument(mapper != null);

    final int size = parameters.size();
    final List<Object> args = new ArrayList<>(size);
    final List<String> infoArgs = new ArrayList<>(size);
    for (ApmType apmType : parameters) {
      Object value = apmType.getValue();
      args.add(value);
      infoArgs.add(String.valueOf(value));
    }

    List<MappingDescriptor> mappingMethods = mapper.getMappings();
    for (MappingDescriptor mappingMethod : mappingMethods) {
      final Type[] parameterTypes = mappingMethod.getMethod().getGenericParameterTypes();
      if (!validParameters(args, parameterTypes)) {
        continue;
      }

      try {
        Action action = (Action) mappingMethod.getMethod().invoke(mapper.getObject(), args.toArray());
        return new ActionDescriptor(command, action, infoArgs);
      } catch (IllegalAccessException e) {
        LOG.error("Cannot access action mapper method: {} while processing command: {}", e.getMessage(), command);
      } catch (InvocationTargetException e) {
        LOG.error("Cannot invoke action mapper method: {} while processing command: {}", e.getMessage(), command);
      }

    }
    throw new ActionCreationException(String.format("Cannot find action for command: %s", command));
  }

  private boolean validParameters(List<Object> values, Type[] parameterTypes) {
    if (values.size() != parameterTypes.length) {
      return false;
    }
    int i = 0;
    for (Type type : parameterTypes) {
      Object value = values.get(i);
      if (!validParameter(value, type)) {
        return false;
      }
      i++;
    }
    return true;
  }

  private boolean validParameter(Object value, Type type) {
    if (value instanceof List && isList(type)) {
      return true;
    } else if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;
      if (value instanceof Number && Number.class.isAssignableFrom(clazz)) {
        return true;
      }
      if (value instanceof Boolean && Boolean.class == clazz) {
        return true;
      }
      if (value instanceof String && String.class == clazz) {
        return true;
      }
    }
    return false;
  }

  private boolean isList(Type type) {
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Class<?> clazz = (Class<?>) parameterizedType.getRawType();
      return List.class == clazz;
    } else if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;
      return List.class == clazz;
    } else {
      return false;
    }
  }
}
