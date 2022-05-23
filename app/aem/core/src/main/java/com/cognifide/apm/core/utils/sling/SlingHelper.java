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

import com.cognifide.apm.core.services.ResourceResolverProvider;
import com.cognifide.apm.core.utils.LogUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SlingHelper {

  private static final Logger LOG = LoggerFactory.getLogger(SlingHelper.class);

  private static final String RESOLVE_ERROR_MESSAGE = "Error occurred while resolving data from repository.";

  private static final String OPERATE_ERROR_MESSAGE = "Error occurred while operating on data from repository.";

  private SlingHelper() {
    // hidden constructor
  }

  /**
   * Retrieve values from repository with wrapped impersonated session (automatically opened and closed).
   */
  @SuppressWarnings("unchecked")
  public static <T> T resolve(ResourceResolverProvider provider, String userId, ResolveCallback callback)
      throws ResolveException {
    try (ResourceResolver resolver = provider.getResourceResolver(userId)) {
      return (T) callback.resolve(resolver);
    } catch (Exception e) {
      throw new ResolveException(RESOLVE_ERROR_MESSAGE, e);
    }
  }

  /**
   * Retrieve values from repository with wrapped session (automatically opened and closed).
   */
  public static <T> T resolveDefault(ResourceResolverProvider provider, ResolveCallback callback,
      T defaultValue) {
    return resolveDefault(provider, null, callback, defaultValue);
  }

  /**
   * Retrieve values from repository with wrapped session (automatically opened and closed).
   */
  public static <T> T resolveDefault(ResourceResolverProvider provider, String userId, ResolveCallback callback,
      T defaultValue) {
    try {
      return resolve(provider, userId, callback);
    } catch (ResolveException e) {
      LOG.error(RESOLVE_ERROR_MESSAGE, e);
    }
    return defaultValue;
  }

  /**
   * Do some operation on repository (delete or update resource etc) with wrapped impersonated session
   * (automatically opened and closed).
   */
  public static void operate(ResourceResolverProvider provider, String userId, OperateCallback callback)
      throws OperateException {
    try (ResourceResolver resolver = provider.getResourceResolver(userId)) {
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
  public static void operateTraced(ResourceResolverProvider provider, OperateCallback callback) {
    operateTraced(provider, null, callback);
  }

  /**
   * Do some operation on repository (delete or update resource etc) with wrapped session (automatically
   * opened and closed).
   */
  public static void operateTraced(ResourceResolverProvider provider, String userId, OperateCallback callback) {
    try {
      operate(provider, userId, callback);
    } catch (OperateException e) {
      LogUtils.log(LOG, String.format("%s %s", OPERATE_ERROR_MESSAGE, e.getMessage()));
      LOG.error(OPERATE_ERROR_MESSAGE, e);
    }
  }

}