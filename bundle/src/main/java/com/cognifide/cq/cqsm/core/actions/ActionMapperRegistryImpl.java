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
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.actions.scanner.AnnotatedClassRegistry;
import com.cognifide.cq.cqsm.core.actions.scanner.RegistryChangedListener;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
@Service(ActionMapperRegistry.class)
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Action mapper registry service"),
    @Property(name = Constants.SERVICE_VENDOR, value = Cqsm.VENDOR_NAME)
})
public class ActionMapperRegistryImpl implements RegistryChangedListener, ActionMapperRegistry {

  private static final Logger LOG = LoggerFactory.getLogger(ActionMapperRegistryImpl.class);

  private static final String BUNDLE_HEADER = "CQ-Security-Management-Actions";

  private AnnotatedClassRegistry registry;

  private volatile AtomicReference<List<Object>> mappers = new AtomicReference<>(Collections.emptyList());

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
    this.mappers.set(ImmutableList.copyOf(createActionMappers(registeredClasses)));
  }

  @Override
  public List<Object> getMappers() {
    return mappers.get();
  }

  private static List<Object> createActionMappers(List<Class<?>> classes) {
    List<Object> mappers = Lists.newArrayListWithExpectedSize(classes.size());
    for (Class clazz : classes) {
      try {
        mappers.add(clazz.newInstance());
      } catch (InstantiationException e) {
        LOG.error("Cannot instantiate action mapper: {}", e.getMessage());
      } catch (IllegalAccessException e) {
        LOG.error("Cannot construct action mapper, is constructor non public? {}", e.getMessage());
      }
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Created {} action mappers from {} classes", mappers.size(), classes.size());
    }
    return mappers;
  }
}
