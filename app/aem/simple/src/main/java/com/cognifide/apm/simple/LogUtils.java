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
package com.cognifide.apm.simple;

import com.day.cq.commons.jcr.JcrConstants;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;

public class LogUtils {

  private static String getInstanceName() {
    return ManagementFactory.getRuntimeMXBean().getName();
  }

  public static void log(Logger logger, ResourceResolver resolver, String message) {
    logger.info(message);
    saveLog(resolver, message, logger.getName());
  }

  public static void log(Logger logger, Session session, String message) {
    logger.info(message);
    saveLog(session, message, logger.getName());
  }

  private static void saveLog(ResourceResolver resolver, String message, String className) {
    try {
      Session session = resolver.adaptTo(Session.class);
      saveLog(session, message, className);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void saveLog(Session session, String message, String className) {
    String instanceName = getInstanceName();
    String executionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss.SSS"));
    try {
      Node node = JcrUtils.getOrCreateByPath("/apps/apm/logs/log", true, JcrConstants.NT_UNSTRUCTURED, JcrConstants.NT_UNSTRUCTURED, session, true);
      node.setProperty("message", message);
      node.setProperty("instanceName", instanceName);
      node.setProperty("executionTime", executionTime);
      node.setProperty("className", className);
      session.save();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
