/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
package com.cognifide.apm.core.crypto;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.endpoints.AbstractFormServlet;
import com.cognifide.apm.core.endpoints.response.ResponseEntity;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.util.Collections;

@Component(
    service = Servlet.class,
    property = {
        Property.PATH + "/bin/apm/scripts/protect",
        Property.METHOD + "POST",
        Property.DESCRIPTION + "APM Encrypt Text Servlet",
        Property.VENDOR
    }
)
public class ProtectTextServlet extends AbstractFormServlet<ProtectTextForm> {

  @Reference
  private CryptoSupport cryptoSupport;

  @Override
  protected ResponseEntity process(ProtectTextForm form, ResourceResolver resolver) throws Exception {
    ResponseEntity responseEntity = null;
    try {
      responseEntity = ResponseEntity.ok("Text successfully encrypted", ImmutableMap.of(
          "text", cryptoSupport.protect(form.getText())
      ));
    } catch (CryptoException e) {
      responseEntity = ResponseEntity.badRequest(StringUtils.defaultString(e.getMessage(), "Errors while encrypting text"), Collections.emptyMap());
    }
    return responseEntity;
  }
}
