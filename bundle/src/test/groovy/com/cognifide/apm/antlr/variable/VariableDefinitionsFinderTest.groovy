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
package com.cognifide.apm.antlr.variable

import com.cognifide.apm.antlr.ApmInteger
import com.cognifide.apm.antlr.ApmList
import com.cognifide.apm.antlr.ApmString
import com.cognifide.apm.antlr.ApmType
import com.cognifide.apm.antlr.parsedscript.ParsedScript
import com.cognifide.cq.cqsm.api.scripts.Script
import spock.lang.Specification

class VariableDefinitionsFinderTest extends Specification {

    def "find variable definitions" (){
        given:
        def underTest = new VariableDefinitionsFinder()

        def script = createScript()

        when:
        def result = underTest.find(script)
        def v = ApmType
        then:
        result == ["varInteger": new ApmInteger(1),
                   "varString":new ApmString("string"),
                   "varList" : new ApmList(["1", "2"]) ]
    }

    private ParsedScript createScript() {
        def content = """ DEFINE varInteger 1
                          DEFINE varString "string"  
                          DEFINE varList ["1", "2"]"""
        def script = Mock(Script)
        script.path >> "/dummy/path"
        script.data >> content

        new ParsedScript.Factory().create(script)
    }
}
