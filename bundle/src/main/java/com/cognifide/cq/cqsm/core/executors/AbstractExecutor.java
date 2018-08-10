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
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;

import javax.jcr.RepositoryException;

abstract class AbstractExecutor {

    @Reference
    private ScriptManager scriptManager;

    @Reference
    ScriptFinder scriptFinder;

    @Reference
    ResourceResolverFactory resolverFactory;

    void processScript(Script script, ResourceResolver resolver, String executorType) throws PersistenceException {
        final String scriptPath = script.getPath();
        try {
            scriptManager.process(script, Mode.VALIDATION, resolver);
            if (script.isValid()) {
                final Progress progress = scriptManager.process(script, Mode.AUTOMATIC_RUN, resolver);
                logStatus(scriptPath, progress.isSuccess(), executorType);
            } else {
                if(getLogger().isWarnEnabled()) {
                    getLogger().warn(String.format("%s executor cannot execute script which is not valid: {}", executorType), scriptPath);
                }
            }
        } catch (RepositoryException e) {
            getLogger().error("Script cannot be processed because of repository error: {}", scriptPath, e);
        }
    }

    private void logStatus(String scriptPath, boolean success, String executorType) {
        if (success && getLogger().isInfoEnabled()) {
            getLogger().info(String.format("%s script successfully executed: {}", executorType), scriptPath);
        } else {
            getLogger().error(String.format("%s script cannot be executed properly: {}", executorType), scriptPath);
        }
    }

    abstract Logger getLogger();
}
