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

import com.google.common.collect.ImmutableMap;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;

public class DefaultHistoryLogStrategy implements HistoryLogStrategy {

  private final String historyPath;

  public DefaultHistoryLogStrategy(String historyPath) {
    this.historyPath = historyPath;
  }

  @Override
  public Resource getHistoryLogResource(ResourceResolver resolver, String fileName)
      throws PersistenceException, RepositoryException {
    Resource historyFolder = getOrCreateHistoryFolder(resolver);
    String uniqueName = ResourceUtil.createUniqueChildName(historyFolder, fileName);
    return resolver.create(historyFolder, uniqueName, ImmutableMap.of("jcr:primaryType", "nt:unstructured"));
  }

  private Resource getOrCreateHistoryFolder(ResourceResolver resolver) throws RepositoryException {
    Session session = resolver.adaptTo(Session.class);
    Node node = JcrUtils.getOrCreateByPath(historyPath, "sling:OrderedFolder", session);
    session.save();
    return resolver.getResource(node.getPath());
  }
}
