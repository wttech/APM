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

import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import org.apache.commons.io.IOUtils
import org.apache.sling.api.resource.ResourceResolver
import spock.lang.Specification

class ReferenceFinderTest extends Specification {

    ScriptFinder scriptFinder = Mock(ScriptFinder)
    ResourceResolver resourceResolver = Mock(ResourceResolver)

    def "return all scripts included and imported to given script (recursively)"() {
        given:
        Script script = createScript("/import-and-run.apm")
        scriptFinder.find("/import-a.apm", resourceResolver) >> createScript("/import-a.apm")
        scriptFinder.find("/import-b.apm", resourceResolver) >> createScript("/import-b.apm")
        scriptFinder.find("/import-c.apm", resourceResolver) >> createScript("/import-c.apm")
        scriptFinder.find("/run-a.apm", resourceResolver) >> createScript("/run-a.apm")
        scriptFinder.find("/run-b.apm", resourceResolver) >> createScript("/run-b.apm")
        scriptFinder.find("/run-c.apm", resourceResolver) >> createScript("/run-c.apm")
        ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resourceResolver)

        when:
        List<Script> references = referenceFinder.findReferences(script)

        then:
        references == [scriptFinder.find("/import-a.apm", resourceResolver),
                       scriptFinder.find("/import-b.apm", resourceResolver),
                       scriptFinder.find("/import-c.apm", resourceResolver),
                       scriptFinder.find("/run-b.apm", resourceResolver),
                       scriptFinder.find("/run-c.apm", resourceResolver),
                       scriptFinder.find("/run-a.apm", resourceResolver)]
    }

    private Script createScript(String file) {
        def content = IOUtils.toString(getClass().getResourceAsStream(file))
        def script = Mock(Script)
        script.path >> file
        script.data >> content
        return script
    }
}
