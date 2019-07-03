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

package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.ApmLangParser.ApmContext
import com.cognifide.apm.antlr.argument.ArgumentResolver
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.core.progress.ProgressImpl

class ExecutionContext(val executor: String, val root: ApmContext) {

    val variableHolder = VariableHolder()
    val argumentResolver = ArgumentResolver(variableHolder)
    val progress: Progress

    init {
        this.progress = ProgressImpl(executor)
    }
}
