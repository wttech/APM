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
package com.cognifide.apm.core.services;

import com.cognifide.apm.core.Property;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.serviceusermapping.ServiceUserMapped;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = {ResourceResolverProvider.class},
    property = {
        Property.DESCRIPTION + "APM Resource Resolver Provider",
        Property.VENDOR
    }
)
public class ResourceResolverProvider {

  private static final String SUBSERVICE_NAME = "apm";

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Reference
  private ServiceUserMapped serviceUserMapped;

  public ResourceResolver getResourceResolver(String userId) throws LoginException {
    ResourceResolver resolver;
    if (userId != null) {
      Map<String, Object> authenticationInfo = Maps.newHashMap();
      authenticationInfo.put(ResourceResolverFactory.USER_IMPERSONATION, userId);
      resolver = resolverFactory.getAdministrativeResourceResolver(authenticationInfo);
    } else {
      Map<String, Object> parameters = new HashMap<>();
      parameters.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_NAME);
      resolver = resolverFactory.getServiceResourceResolver(parameters);
    }
    return resolver;
  }

}
