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

package com.cognifide.cq.cqsm.core.history;

import static com.day.crx.JcrConstants.JCR_PRIMARYTYPE;
import static com.day.crx.JcrConstants.NT_UNSTRUCTURED;

import com.google.common.collect.ImmutableMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;

public class DryRunHistoryEntryNamingStrategy implements HistoryEntryNamingStrategy {

  @Override
  public Resource getHistoryEntryResource(ResourceResolver resolver, Resource historyFolder, String fileName)
      throws PersistenceException {
    String uniqueName = ResourceUtil.createUniqueChildName(historyFolder, "dryRun-" + fileName);
    Resource historyLogResource = resolver.getResource(historyFolder, uniqueName);
    if (historyLogResource == null) {
      historyLogResource = resolver
          .create(historyFolder, uniqueName, ImmutableMap.of(JCR_PRIMARYTYPE, NT_UNSTRUCTURED));
    }
    return historyLogResource;
  }

}
