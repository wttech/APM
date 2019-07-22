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

import com.cognifide.apm.antlr.argument.Arguments;
import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.cq.cqsm.api.actions.ActionMapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import com.cognifide.cq.cqsm.core.Property;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = ActionFactory.class,
    property = {
        Property.DESCRIPTION + "Action factory service",
        Property.VENDOR
    }
)
public class ActionFactoryImpl implements ActionFactory {

  @Reference
  private ActionMapperRegistry registry;

  public ActionDescriptor evaluate(String command, Arguments arguments) throws ActionCreationException {
    Optional<MapperDescriptor> mapper = registry.getMapper(command);
    if (mapper.isPresent()) {
      return new ActionDescriptor(command, tryToEvaluateCommand(mapper.get(), arguments));
    }
    throw new ActionCreationException(String.format("Cannot find action for command: %s", command));
  }

  private Action tryToEvaluateCommand(MapperDescriptor mapper, Arguments arguments)
      throws ActionCreationException {
    if (mapper.handles(arguments)) {
      return mapper.handle(arguments);
    }
    throw new ActionCreationException("Mapper cannot handle given arguments: " + arguments);
  }

  @Override
  public List<Map<String, Object>> refer() {
    final List<Map<String, Object>> references = new ArrayList<>();

    for (Object mapper : registry.getMappers()) {
      for (Method method : mapper.getClass().getDeclaredMethods()) {
        if (!method.isAnnotationPresent(Mapping.class) || !(mapper instanceof ActionMapper)) {
          continue;
        }

        final Mapping mapping = method.getAnnotation(Mapping.class);
        final List<String> commands = ((ActionMapper) mapper).referMapping(mapping);

        HashMap<String, Object> reference = new HashMap<>();
        reference.put("commands", commands);
        reference.put("pattern", mapping.value());
        reference.put("args", mapping.args());
        reference.put("reference", mapping.reference());

        references.add(reference);
      }
    }

    sortReferences(references);

    return references;
  }

  private void sortReferences(List<Map<String, Object>> references) {
    Collections.sort(references, (object1, object2) -> {
      List<String> commands1 = (List<String>) object1.get("commands");
      List<String> commands2 = (List<String>) object2.get("commands");
      String command1 = commands1.get(0);
      String command2 = commands2.get(0);
      return command1.compareToIgnoreCase(command2);
    });
  }
}
