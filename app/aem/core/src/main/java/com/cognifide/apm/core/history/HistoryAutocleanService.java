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

package com.cognifide.apm.core.history;

import com.cognifide.apm.core.utils.sling.SlingHelper;
import java.time.LocalDate;
import java.util.stream.StreamSupport;
import javax.jcr.query.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Slf4j
@Component(
    immediate = true,
    service = Runnable.class
)
@Designate(ocd = HistoryAutocleanService.Config.class)
public class HistoryAutocleanService implements Runnable {

  private static final String QUERY = "SELECT * FROM [nt:unstructured] "
      + "WHERE ISDESCENDANTNODE('" + HistoryImpl.HISTORY_FOLDER + "') "
      + "AND executionTime IS NOT NULL";

  private static final String MAX_DAYS_QUERY = QUERY + " AND executionTime <= '%s'";

  private static final String ORDERED_QUERY = QUERY + " ORDER BY executionTime DESC";

  private Config config;

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Activate
  @Modified
  public void activate(Config config) {
    this.config = config;
  }

  @Override
  public void run() {
    SlingHelper.operateTraced(resolverFactory, this::deleteHistoryByEntries);
    SlingHelper.operateTraced(resolverFactory, this::deleteHistoryByDays);
  }

  private void deleteHistoryByEntries(ResourceResolver resolver) {
    if (config.maxEntries() >= 0) {
      log.info("Looking for items exceeding limit of {} items", config.maxEntries());
      deleteHistoryByQuery(resolver, ORDERED_QUERY, config.maxEntries());
    }
  }

  private void deleteHistoryByDays(ResourceResolver resolver) {
    if (config.maxDays() >= 0) {
      log.info("Looking for items older than {} days", config.maxDays());
      LocalDate date = LocalDate.now().minusDays(config.maxDays());
      deleteHistoryByQuery(resolver, String.format(MAX_DAYS_QUERY, date), 0);
    }
  }

  private void deleteHistoryByQuery(ResourceResolver resolver, String query, int offset) {
    Iterable<Resource> iterable = () -> resolver.findResources(query, Query.JCR_SQL2);
    StreamSupport.stream(iterable.spliterator(), false)
        .skip(offset)
        .forEach(resource -> deleteItem(resolver, resource));
  }

  private void deleteItem(ResourceResolver resolver, Resource resource) {
    try {
      log.info("Deleting: {}", resource.getPath());
      resolver.delete(resource);
    } catch (PersistenceException e) {
      throw new RuntimeException(e);
    }
  }

  @ObjectClassDefinition(name = "AEM Permission Management - History Auto-clean Configuration")
  public @interface Config {

    @AttributeDefinition(
        name = "Entries",
        type = AttributeType.INTEGER,
        description = "Max number of entries",
        defaultValue = "-1"
    )
    int maxEntries();

    @AttributeDefinition(
        name = "Days",
        type = AttributeType.INTEGER,
        description = "How many days to keep in history",
        defaultValue = "-1"
    )
    int maxDays();

    @AttributeDefinition(
        name = "Expression",
        description = "CRON expression"
    )
    String scheduler_expression();

  }

}