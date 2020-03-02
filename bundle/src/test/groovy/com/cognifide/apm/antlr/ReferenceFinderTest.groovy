package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.executioncontext.ExecutionContext
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import com.cognifide.cq.cqsm.core.progress.ProgressImpl
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

        ExecutionContext executionContext = ExecutionContext.create(scriptFinder, resourceResolver, script, new ProgressImpl(""))
        ReferenceFinder referenceFinder = new ReferenceFinder(executionContext)
        ApmLangParser parser = ApmLangParserHelper.createParserUsingFile("/import-and-run.apm")

        when:
        List<Script> references = referenceFinder.findReferences(parser.apm())

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
