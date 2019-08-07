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

package com.cognifide.cq.cqsm.core.actions

import com.cognifide.cq.cqsm.api.actions.Action
import com.cognifide.cq.cqsm.api.actions.ActionResult
import com.cognifide.cq.cqsm.api.actions.annotations.Flags
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping
import com.cognifide.cq.cqsm.api.actions.annotations.Named
import com.cognifide.cq.cqsm.api.exceptions.ActionExecutionException
import com.cognifide.cq.cqsm.api.executors.Context

@Mapper("sample")
class SampleMapper {

    @Mapping("")
    Action create1(String path, String permissions,
                   @Named("glob") String glob, @Named("types") List<String> types, @Named("items") List<String> items,
                   @Flags List<String> flags) {
        return new DummyAction("create1")
    }

    @Mapping("")
    Action create2(String path, List<String> permissions,
                   @Named("glob") String glob, @Named("types") List<String> types, @Named("items") List<String> items,
                   @Flags List<String> flags) {
        return new DummyAction("create2")
    }

    Action create3(String path, List<String> permissions,
                   @Named("glob") String glob, @Named("types") List<String> types, @Named("items") List<String> items,
                   @Flags List<String> flags) {
        return new DummyAction("create3")
    }

    class DummyAction implements Action {

        String name

        DummyAction(String name) {
            this.name = name
        }

        @Override
        ActionResult simulate(Context context) throws ActionExecutionException {
            return null
        }

        @Override
        ActionResult execute(Context context) throws ActionExecutionException {
            return null
        }

        @Override
        boolean isGeneric() {
            return false
        }
    }
}
