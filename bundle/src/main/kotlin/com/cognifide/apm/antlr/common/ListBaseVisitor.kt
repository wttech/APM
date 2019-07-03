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

package com.cognifide.apm.antlr.common

import com.cognifide.apm.antlr.ApmLangBaseVisitor

open class ListBaseVisitor<T> : ApmLangBaseVisitor<MutableList<T>>() {

    override fun defaultResult(): MutableList<T> {
        return mutableListOf()
    }

    override fun aggregateResult(aggregate: MutableList<T>, nextResult: MutableList<T>?): MutableList<T> {
        if (nextResult != null) {
            aggregate.addAll(nextResult)
        }
        return aggregate
    }
}
