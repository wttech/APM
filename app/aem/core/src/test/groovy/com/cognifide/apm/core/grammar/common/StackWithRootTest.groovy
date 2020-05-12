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

package com.cognifide.apm.core.grammar.common

import com.google.common.collect.Lists
import spock.lang.Specification

class StackWithRootTest extends Specification {

    def "returns elements in reversed order"() {
        when:
        def stack = new StackWithRoot<String>("root")
        stack.push("1")
        stack.push("2")
        stack.push("3")

        def newList = Lists.newArrayList(stack)

        then:
        newList == ["3", "2", "1", "root"]
    }

    def "cannot remove root element"() {
        when:
        def stack = new StackWithRoot<String>("root")
        stack.push("1")
        stack.push("2")
        stack.push("3")

        then:
        stack.pop() == "3"
        stack.pop() == "2"
        stack.pop() == "1"
        stack.pop() == "root"
        stack.pop() == "root"
        stack.pop() == "root"
    }
}
