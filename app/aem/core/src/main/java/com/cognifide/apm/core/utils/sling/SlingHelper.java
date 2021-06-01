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
package com.cognifide.apm.core.utils.sling;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

@Slf4j
public final class SlingHelper {

  private static final String RESOLVE_ERROR_MESSAGE = "Error occurred while resolving data from repository.";

  private static final String OPERATE_ERROR_MESSAGE = "Error occurred while operating on data from repository.";

  private static final String SUBSERVICE_NAME = "apm";

  private SlingHelper() {
    // hidden constructor
  }

  /**
   * Retrieve values from repository with wrapped impersonated session (automatically opened and closed).
   */
  @SuppressWarnings("unchecked")
  public static <T> T resolve(ResourceResolverFactory factory, String userId, ResolveCallback callback)
      throws ResolveException {
    try (ResourceResolver resolver = getResourceResolverForUser(factory, userId)) {
      return (T) callback.resolve(resolver);
    } catch (Exception e) {
      throw new ResolveException(RESOLVE_ERROR_MESSAGE, e);
    }
  }

  /**
   * Retrieve values from repository with wrapped session (automatically opened and closed).
   */
  public static <T> T resolveDefault(ResourceResolverFactory factory, ResolveCallback callback,
                                     T defaultValue) {
    return resolveDefault(factory, null, callback, defaultValue);
  }

  /**
   * Retrieve values from repository with wrapped session (automatically opened and closed).
   */
  public static <T> T resolveDefault(ResourceResolverFactory factory, String userId, ResolveCallback callback,
                                     T defaultValue) {
    try {
      return resolve(factory, userId, callback);
    } catch (ResolveException e) {
      log.error(RESOLVE_ERROR_MESSAGE, e);
    }

    return defaultValue;
  }

  /**
   * Do some operation on repository (delete or update resource etc) with wrapped impersonated session
   * (automatically opened and closed).
   */
  public static void operate(ResourceResolverFactory factory, String userId, OperateCallback callback)
      throws OperateException {
    try (ResourceResolver resolver = getResourceResolverForUser(factory, userId)) {
      callback.operate(resolver);
      resolver.commit();
    } catch (Exception e) {
      throw new OperateException(OPERATE_ERROR_MESSAGE, e);
    }
  }

  /**
   * Do some operation on repository (delete or update resource etc) with wrapped session (automatically
   * opened and closed).
   */
  public static void operateTraced(ResourceResolverFactory factory, OperateCallback callback) {
    operateTraced(factory, null, callback);
  }

  /**
   * Do some operation on repository (delete or update resource etc) with wrapped session (automatically
   * opened and closed).
   */
  public static void operateTraced(ResourceResolverFactory factory, String userId, OperateCallback callback) {
    try {
      operate(factory, userId, callback);
    } catch (OperateException e) {
      log.error(OPERATE_ERROR_MESSAGE, e);
    }
  }

  /**
   * Create a new session for specified user (impersonating).
   */
  public static ResourceResolver getResourceResolverForUser(ResourceResolverFactory factory, String userId)
      throws LoginException {
    ResourceResolver resolver;
    if (userId != null) {
      Map<String, Object> authenticationInfo = Maps.newHashMap();
      authenticationInfo.put(ResourceResolverFactory.USER_IMPERSONATION, userId);
      resolver = factory.getAdministrativeResourceResolver(authenticationInfo);
    } else {
      Map<String, Object> parameters = new HashMap<>();
      parameters.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_NAME);
      resolver = factory.getServiceResourceResolver(parameters);
    }

    return resolver;
  }

  public static ResourceResolver getResourceResolverForService(ResourceResolverFactory factory)
      throws LoginException {
    return getResourceResolverForUser(factory, null);
  }
}