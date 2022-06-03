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

import javax.jcr.Session;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.serviceusermapping.ServiceUserMapped;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    service = ApmSimpleMappedService.class
)
public class ApmSimpleMappedService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApmSimpleMappedService.class);

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private ServiceUserMapped serviceUserMapped;

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private SlingRepository slingRepository;

  @Activate
  public void activate() {
    Session session = null;
    try {
      session = slingRepository.loginService(null, null);
      LogUtils.log(LOGGER, session, "test session mapped service");
    } catch (Exception e) {
      LOGGER.error("", e);
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

}
