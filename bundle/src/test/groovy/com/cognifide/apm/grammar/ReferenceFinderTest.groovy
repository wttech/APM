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

package com.cognifide.apm.grammar

import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import org.apache.commons.io.IOUtils
import org.apache.sling.api.resource.ResourceResolver
import spock.lang.Specification

class ReferenceFinderTest extends Specification {

    ScriptFinder scriptFinder = Stub(ScriptFinder) {
        find(_, _) >> {
            args -> createScript(args[0])
        }
    }
    ResourceResolver resourceResolver = Mock(ResourceResolver)

    def "return all scripts included and imported to given script (recursively)"() {
        given:
        Script script = createScript("/import-and-run1.apm")
        ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resourceResolver)

        when:
        List<Script> references = referenceFinder.findReferences(script)

        then:
        references.collect { it.toString() } == [
                "/includes/import-a.apm",
                "/includes/import-b.apm",
                "/includes/import-c.apm",
                "/includes/run-b.apm",
                "/includes/run-c.apm",
                "/includes/run-a.apm"
        ]
    }

    def "return reference graph for given script"() {
        given:
        Script script = createScript("/import-and-run1.apm")
        ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resourceResolver)

        when:
        ReferenceGraph referenceGraph = referenceFinder.getReferenceGraph(script)

        then:
        printReferenceGraph(referenceGraph) == """\
            /import-and-run1.apm
            |- /includes/import-a.apm
            |  |- /includes/import-b.apm
            |  |  |- /includes/import-c.apm
            |  |- /includes/run-b.apm
            |  |  |- /includes/import-b.apm
            |  |  |  |- /includes/import-c.apm
            |  |  |- /includes/run-c.apm
            |- /includes/import-b.apm
            |  |- /includes/import-c.apm
            |- /includes/run-a.apm
        """.stripIndent()
    }

    def "return reference graph for given scripts"() {
        given:
        Script script1 = createScript("/import-and-run1.apm")
        Script script2 = createScript("/import-and-run2.apm")
        ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resourceResolver)

        when:
        ReferenceGraph referenceGraph = referenceFinder.getReferenceGraph(script1, script2)

        then:
        printReferenceGraph(referenceGraph) == """\
            /import-and-run1.apm
            |- /includes/import-a.apm
            |  |- /includes/import-b.apm
            |  |  |- /includes/import-c.apm
            |  |- /includes/run-b.apm
            |  |  |- /includes/import-b.apm
            |  |  |  |- /includes/import-c.apm
            |  |  |- /includes/run-c.apm
            |- /includes/import-b.apm
            |  |- /includes/import-c.apm
            |- /includes/run-a.apm
            /import-and-run2.apm
            |- /includes/cycle-a.apm
            |  |- /includes/cycle-b.apm
            |  |  |- /includes/cycle-c.apm
            |  |  |  |- /includes/cycle-a.apm <error - found cycle>
            |- /includes/run-a.apm
        """.stripIndent()
    }

    private Script createScript(String file) {
        def content = IOUtils.toString(getClass().getResourceAsStream(file))
        def script = Mock(Script)
        script.path >> file
        script.data >> content
        script.toString() >> file
        return script
    }

    private String printReferenceGraph(ReferenceGraph referenceGraph) {
        StringBuilder result = new StringBuilder()
        referenceGraph.roots.forEach {
            result.append(it.script.path).append("\n")
            appendChildren(result, "|- ", it.children)
        }
        return result.toString()
    }


    private void appendChildren(StringBuilder result, String prefix, List<ReferenceGraph.TreeNode> children) {
        children.forEach {
            result.append(prefix).append(it.script.path)
            if (it instanceof ReferenceGraph.CyclicNode) {
                result.append(" <error - found cycle>")
            }
            result.append("\n")
            appendChildren(result, "|  ${prefix}", it.children)
        }
    }
}
