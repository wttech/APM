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

package com.cognifide.apm.core.grammar


import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.grammar.ReferenceGraph.Transition
import com.cognifide.apm.core.grammar.ReferenceGraph.TreeNode
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

    def "return all scripts included and imported to given script (recursively) including current script"() {
        given:
        Script script = createScript("/import-and-run3.apm")
        ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resourceResolver)

        when:
        List<Script> references = referenceFinder.findReferences(script)

        then:
        references.collect { it.toString() } == [
                "/import-and-run3.apm",
                "/includes/import-a.apm",
                "/includes/import-b.apm",
                "/includes/import-c.apm",
                "/includes/run-b.apm",
                "/includes/run-c.apm"
        ]
    }

    def "cycle found in script's tree"() {
        given:
        Script script = createScript("/import-and-run2.apm")
        ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resourceResolver)

        when:
        referenceFinder.findReferences(script)

        then:
        def e = thrown(ScriptExecutionException.class)
        e.message == "Cycle detected /includes/cycle-c.apm -> /includes/cycle-a.apm"
    }

    def "ERROR found in script's tree"() {
        given:
        Script script = createScript("/import-and-run1.apm")
        ReferenceFinder referenceFinder = new ReferenceFinder(scriptFinder, resourceResolver)

        when:
        referenceFinder.findReferences(script)

        then:
        def e = thrown(ScriptExecutionException.class)
        e.message == "Script doesn't exist /includes/non-existing.apm"
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
        |-- IMPORT --> /includes/import-a.apm
        | |-- IMPORT --> /includes/import-b.apm
        | | |-- IMPORT --> /includes/import-c.apm
        | |-- RUN_SCRIPT --> /includes/run-b.apm
        | | |-- IMPORT --> /includes/import-b.apm
        | | | |-- IMPORT --> /includes/import-c.apm
        | | |-- RUN_SCRIPT --> /includes/run-c.apm
        |-- IMPORT --> /includes/import-b.apm
        | |-- IMPORT --> /includes/import-c.apm
        |-- RUN_SCRIPT --> /includes/run-a.apm
        | |-- IMPORT --> /includes/non-existing.apm <error - script doesn't exist>
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
        println printReferenceGraph(referenceGraph)
        printReferenceGraph(referenceGraph) == """\
            /import-and-run1.apm
            |-- IMPORT --> /includes/import-a.apm
            | |-- IMPORT --> /includes/import-b.apm
            | | |-- IMPORT --> /includes/import-c.apm
            | |-- RUN_SCRIPT --> /includes/run-b.apm
            | | |-- IMPORT --> /includes/import-b.apm
            | | | |-- IMPORT --> /includes/import-c.apm
            | | |-- RUN_SCRIPT --> /includes/run-c.apm
            |-- IMPORT --> /includes/import-b.apm
            | |-- IMPORT --> /includes/import-c.apm
            |-- RUN_SCRIPT --> /includes/run-a.apm
            | |-- IMPORT --> /includes/non-existing.apm <error - script doesn't exist>
            /import-and-run2.apm
            |-- RUN_SCRIPT --> /includes/cycle-a.apm
            | |-- RUN_SCRIPT --> /includes/cycle-b.apm
            | | |-- RUN_SCRIPT --> /includes/cycle-c.apm
            | | | |-- RUN_SCRIPT --> /includes/cycle-a.apm <error - cycle detected>
            |-- RUN_SCRIPT --> /includes/run-a.apm
            | |-- IMPORT --> /includes/non-existing.apm <error - script doesn't exist>
            """.stripIndent()
    }

    private Script createScript(String file) {
        def stream = getClass().getResourceAsStream(file)
        if (stream == null) {
            return null
        }
        def content = IOUtils.toString(stream)
        def script = Mock(Script)
        script.path >> file
        script.data >> content
        script.toString() >> file
        return script
    }

    private String printReferenceGraph(ReferenceGraph referenceGraph) {
        StringBuilder result = new StringBuilder()

        Set<TreeNode> visited = new HashSet<>()
        referenceGraph.nodes.forEach { node ->
            if (!visited.contains(node)) {
                visited.add(node)
                result.append(printNode(node)).append("\n")
                printSubtree(referenceGraph, node, visited, "", result)
            }
        }
        return result.toString()
    }

    private printSubtree(ReferenceGraph referenceGraph, TreeNode fromNode, visited, String prefix, StringBuilder result) {
        referenceGraph.transitions.findAll { transition ->
            transition.from == fromNode
        }.forEach {
            result.append(printTransition(it, prefix))
            if (!it.cycleDetected) {
                printSubtree(referenceGraph, it.to, visited, "| ${prefix}", result)
            }
            visited.add(it.to)
        }
    }

    private String printTransition(Transition transition, String prefix) {
        return "${prefix}|-- ${transition.transitionType} --> ${printNode(transition.to)}${transition.cycleDetected ? ' <error - cycle detected>' : ''}\n"
    }

    private static String printNode(treeNode) {
        if (treeNode instanceof ReferenceGraph.NonExistingTreeNode) {
            return treeNode.script.path + " <error - script doesn't exist>"
        }
        return treeNode.script.path
    }
}
