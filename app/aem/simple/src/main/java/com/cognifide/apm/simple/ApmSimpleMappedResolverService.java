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

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.serviceusermapping.ServiceUserMapped;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Designate(ocd = ApmSimpleMappedResolverService.Configuration.class)
public class ApmSimpleMappedResolverService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApmSimpleMappedResolverService.class);

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private ServiceUserMapped serviceUserMapped;

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverFactory resolverFactory;

  @Activate
  public void activate(Configuration config) {
    try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {
      LogUtils.log(LOGGER, resolver, "test mapped resolver service");
    } catch (Exception e) {
      LOGGER.error("", e);
    }
  }

  @ObjectClassDefinition
  public @interface Configuration {
    @AttributeDefinition
    boolean enabled() default true;
  }

}
