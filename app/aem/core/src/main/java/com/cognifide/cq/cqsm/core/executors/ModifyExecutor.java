/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.cq.cqsm.core.executors;

import static com.cognifide.cq.cqsm.core.scripts.ScriptFilters.onModify;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptManager;
import com.cognifide.apm.core.services.ModifiedScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.EventListener;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.utils.MessagingUtils;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import java.util.List;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    property = {
        Property.DESCRIPTION + "CQSM Script Modification Executor",
        Property.VENDOR
    }
)
public class ModifyExecutor extends AbstractExecutor {

  /**
   * Reference needed for proper event hook up on activation
   */
  @Reference
  private EventListener eventListener;

  @Reference
  private ScriptManager scriptManager;

  @Reference
  private ModifiedScriptFinder modifiedScriptFinder;

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Activate
  private synchronized void activate() {
    SlingHelper.operateTraced(resolverFactory, this::runModified);
  }

  private void runModified(ResourceResolver resolver) throws PersistenceException {
    final List<Script> scripts = modifiedScriptFinder.findAll(onModify(), resolver);
    if (!scripts.isEmpty()) {
      logger.info("Executor will try to run following scripts: {}", scripts.size());
      logger.info(MessagingUtils.describeScripts(scripts));
      for (Script script : scripts) {
        processScript(script, resolver, ExecutorType.MODIFY);
      }
    } else {
      logger.info("Executor has not detected any changes");
    }
  }

  @Override
  public ScriptManager getScriptManager() {
    return scriptManager;
  }
}
