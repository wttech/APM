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
package com.cognifide.apm.core.utils;

import java.lang.management.ManagementFactory;
import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

public final class RuntimeUtils {

  private static final String AEM_MUTABLE_CONTENT_INSTANCE = "aem-install-mutable-content";

  private RuntimeUtils() {
    // intentionally empty
  }

  public static boolean determineCompositeNodeStore(ResourceResolver resolver) {
    boolean result;
    try {
      Session session = resolver.adaptTo(Session.class);
      String path = "/apps";
      Node node = session.getNode(path);
      boolean hasPermission = session.hasPermission("/", Session.ACTION_SET_PROPERTY);
      boolean hasCapability = session.hasCapability("addNode", node, new Object[]{"nt:folder"});
      result = hasPermission && !hasCapability;
    } catch (Exception e) {
      result = false;
    }
    return result;
  }

  public static boolean isMutableContentInstance(ResourceResolver resolver) {
    boolean compositeNodeStore = RuntimeUtils.determineCompositeNodeStore(resolver);
    String instanceName = ManagementFactory.getRuntimeMXBean().getName();
    return !compositeNodeStore || StringUtils.contains(instanceName, AEM_MUTABLE_CONTENT_INSTANCE);
  }
}
