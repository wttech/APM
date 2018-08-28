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

import com.cognifide.cq.cqsm.api.actions.ActionDescriptor;
import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.actions.scanner.AnnotatedClassRegistry;
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    service = ActionFactory.class,
    property = {
        Property.DESCRIPTION + "Action factory service",
        Property.VENDOR
    }
)
public class ActionFactoryService implements ActionFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ActionFactoryService.class);
  private static final String BUNDLE_HEADER = "CQ-Security-Management-Actions";

  private final Map<String, MapperDescriptor> mappers = new ConcurrentHashMap<>();
  private final ActionDescriptionFactory actionDescriptionFactory = new ActionDescriptionFactory();

  private AnnotatedClassRegistry registry;
  private boolean updated = false;

  @Activate
  public void activate(ComponentContext componentContext) {
    registry = new AnnotatedClassRegistry(componentContext.getBundleContext(), BUNDLE_HEADER, Mapper.class);
    registry.open();
  }

  @Deactivate
  public void deactivate() {
    if (registry != null) {
      registry.close();
    }
  }

  @Override
  public ActionDescriptor evaluate(String command, Parameters parameters) throws ActionCreationException {
    MapperDescriptor mapper = getMappers().get(command.toUpperCase());
    if (mapper == null) {
      throw new ActionCreationException("Cannot find mapper for command: " + command.toUpperCase());
    }
    return actionDescriptionFactory.evaluate(mapper, command, parameters);
  }

  @Override
  public synchronized boolean update() {
    if (registry == null) {
      LOG.error("Cannot update action factory (bundle not activated)");
      return false;
    }

    final List<Class<?>> classes = registry.getClasses();

    mappers.clear();
    for (Class clazz : classes) {
      try {
        MapperDescriptor mapperDescriptor = MapperDescriptorFactory.create(clazz);
        mappers.put(mapperDescriptor.getCommandName().toUpperCase(), mapperDescriptor);
      } catch (CannotCreateMapperException e) {
        LOG.error("Cannot create action mapper: {}", e.getMessage());
      }
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Created {} action mappers from {} classes", mappers.size(), classes.size());
    }
    return true;
  }

  @Override
  public List<Map<String, Object>> refer() {
    final List<Map<String, Object>> references = new ArrayList<>();

    for (MapperDescriptor mapper : getMappers().values()) {
      for (MappingDescriptor mapping : mapper.getMappings()) {
        references.add(createReference(mapper, mapping));
      }
    }

    sortReferences(references);
    return references;
  }

  private Map<String, Object> createReference(MapperDescriptor mapper, MappingDescriptor mapping) {
    Map<String, Object> reference = new HashMap<>();
    reference.put("commands", mapper.getCommandName().toUpperCase());
    reference.put("pattern", "");
    reference.put("args", mapping.getAnnotation().args());
    reference.put("reference", mapping.getAnnotation().reference());
    return reference;
  }

  private void sortReferences(List<Map<String, Object>> references) {
    Collections.sort(references, (object1, object2) -> {
      String command1 = (String) object1.get("commands");
      String command2 = (String) object2.get("commands");
      return command1.compareToIgnoreCase(command2);
    });
  }

  protected synchronized Map<String, MapperDescriptor> getMappers() {
    if (!updated) {
      update();
      updated = true;
    }

    return mappers;
  }

}
