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

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractExecutor {

    final Logger logger;

    @Reference
    private ScriptManager scriptManager;

    @Reference
    ScriptFinder scriptFinder;

    @Reference
    ResourceResolverFactory resolverFactory;

    AbstractExecutor() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    void processScript(Script script, ResourceResolver resolver, ExecutorType executorType) throws PersistenceException {
        final String scriptPath = script.getPath();
        try {
            scriptManager.process(script, Mode.VALIDATION, resolver);
            if (script.isValid()) {
                final Progress progress = scriptManager.process(script, Mode.AUTOMATIC_RUN, resolver);
                logStatus(scriptPath, progress.isSuccess(), executorType);
            } else {
                logger.warn("{} executor cannot execute script which is not valid: {}", executorType.toString(), scriptPath);
            }
        } catch (RepositoryException e) {
            logger.error("Script cannot be processed because of repository error: {}", scriptPath, e);
        }
    }

    private void logStatus(String scriptPath, boolean success, ExecutorType executorType) {
        if (success) {
            logger.info("{} script successfully executed: {}", executorType.toString(), scriptPath);
        } else {
            logger.error("{} script cannot be executed properly: {}", executorType.toString(), scriptPath);
        }
    }
}
