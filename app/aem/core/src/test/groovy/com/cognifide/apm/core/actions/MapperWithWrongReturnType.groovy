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

package com.cognifide.apm.core.actions


import com.cognifide.apm.api.actions.annotations.Flags
import com.cognifide.apm.api.actions.annotations.Mapper
import com.cognifide.apm.api.actions.annotations.Mapping
import com.cognifide.apm.api.actions.annotations.Named

@Mapper("mapper")
class MapperWithWrongReturnType {

    @Mapping
    ActionDescriptor create(String path, List<String> permissions,
                            @Named("glob") String glob, @Named("types") List<String> types, @Named("items") List<String> items,
                            @Flags List<String> flags) {
        return null
    }
}
