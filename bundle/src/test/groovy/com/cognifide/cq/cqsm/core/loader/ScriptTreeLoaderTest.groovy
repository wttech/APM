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

package com.cognifide.cq.cqsm.core.loader

import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.core.antlr.InvalidSyntaxException
import com.cognifide.cq.cqsm.core.antlr.InvalidSyntaxMessageFactory
import org.apache.commons.io.IOUtils
import spock.lang.Specification
import spock.lang.Unroll

class ScriptTreeLoaderTest extends Specification {

    @Unroll
    def "run script #file"(String file, String output) {
        given:
        def script = Mock(Script)
        script.data >> IOUtils.toString(getClass().getResourceAsStream(file))
        script.path >> file
        def loader = new ScriptTreeLoader(null, null)

        when:
        loader.loadScriptTree(script)

        then:
        def e = thrown(InvalidSyntaxException)
        InvalidSyntaxMessageFactory.detailedSyntaxError(e) == output

        where:
        file            | output
        "/invalid1.apm" | "Invalid line: DEFINE \$ nana\nInvalid sequence: \$"
        "/invalid2.apm" | "Invalid line: DEFINE / nana\nInvalid sequence: /"
    }
}
