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

package com.cognifide.cq.cqsm.core.actions;

import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.exceptions.InvalidActionMapperException;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.actions.scanner.AnnotatedClassRegistry;
import com.cognifide.cq.cqsm.core.actions.scanner.RegistryChangedListener;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.scribe.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    service = ActionMapperRegistry.class,
    property = {
        Property.DESCRIPTION + "Action mapper registry service",
        Property.VENDOR
    }
)
public class ActionMapperRegistryImpl implements RegistryChangedListener, ActionMapperRegistry {

  private static final Logger LOG = LoggerFactory.getLogger(ActionMapperRegistryImpl.class);

  private static final String BUNDLE_HEADER = "CQ-Security-Management-Actions";

  private AnnotatedClassRegistry registry;

  private volatile AtomicReference<Map<String, MapperDescriptor>> mappers = new AtomicReference<>(
      Collections.emptyMap());

  @Activate
  public void activate(ComponentContext componentContext) {
    registry = new AnnotatedClassRegistry(componentContext.getBundleContext(), BUNDLE_HEADER, Mapper.class);
    registry.addChangeListener(this);
    registry.open();
  }

  @Deactivate
  public void deactivate() {
    if (registry != null) {
      registry.close();
    }
  }

  @Override
  public void registryChanged(List<Class<?>> registeredClasses) {
    this.mappers.set(ImmutableMap.copyOf(createActionMappers(registeredClasses)));
  }

  @Override
  public Optional<MapperDescriptor> getMapper(String name) {
    Preconditions.checkNotNull(name, "Name cannot be null");
    return Optional.ofNullable(mappers.get().get(name.trim().toLowerCase()));
  }

  @Override
  public Collection<MapperDescriptor> getMappers() {
    return mappers.get().values()
        .stream()
        .collect(Collectors.toList());
  }

  private static Map<String, MapperDescriptor> createActionMappers(List<Class<?>> classes) {
    MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory();
    Map<String, MapperDescriptor> mappers = Maps.newHashMapWithExpectedSize(classes.size());
    for (Class clazz : classes) {
      try {
        MapperDescriptor mapperDescriptor = mapperDescriptorFactory.create(clazz);
        mappers.put(mapperDescriptor.getName(), mapperDescriptor);
      } catch (InvalidActionMapperException e) {
        LOG.warn("Cannot register ActionMapper of class " + clazz.getName(), e);
      }
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Created {} action mappers from {} classes", mappers.size(), classes.size());
    }
    return mappers;
  }
}
