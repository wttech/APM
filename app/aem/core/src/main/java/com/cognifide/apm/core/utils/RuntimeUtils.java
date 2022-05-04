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

import javax.jcr.Node;
import javax.jcr.Session;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuntimeUtils {

  public static boolean determineCompositeNodeStore(Session session) {
    try {
      String pathToCheck = "/apps";
      Node appsNode = session.getNode(pathToCheck);
      boolean hasPermission = session.hasPermission("/", Session.ACTION_SET_PROPERTY);
      boolean hasCapability = session.hasCapability("addNode", appsNode, new Object[]{"nt:folder"});
      return hasPermission && !hasCapability;
    } catch (Exception e) {
      throw new IllegalStateException("Could not check if session is connected to a composite node store: " + e, e);
    }
  }

}
