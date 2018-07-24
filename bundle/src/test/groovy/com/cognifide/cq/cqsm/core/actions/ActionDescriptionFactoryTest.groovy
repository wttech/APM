package com.cognifide.cq.cqsm.core.actions

import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters
import com.cognifide.cq.cqsm.core.antlr.type.ApmList
import com.cognifide.cq.cqsm.core.antlr.type.ApmString
import com.cognifide.cq.cqsm.foundation.actions.allow.AllowMapper
import spock.lang.Specification

class ActionDescriptionFactoryTest extends Specification {

    def "creates action description - the longest set of parameters"() {
        given:
        MapperDescriptor mapper = MapperDescriptorFactory.create(AllowMapper.class)
        ActionDescriptionFactory factory = new ActionDescriptionFactory()
        Parameters parameters = new Parameters([
                new ApmString("/path"),
                new ApmString("glob"),
                new ApmList([new ApmString("read")]),
                new ApmString("if_exists")
        ])

        when:
        def descriptor = factory.evaluate(mapper, "allow", parameters)

        then:
        descriptor.command == "allow"
        descriptor.argsToString() == "[/path, glob, [read], if_exists]"
    }

    def "creates action description"() {
        given:
        MapperDescriptor mapper = MapperDescriptorFactory.create(AllowMapper.class)
        ActionDescriptionFactory factory = new ActionDescriptionFactory()
        Parameters parameters = new Parameters([
                new ApmString("/path"),
                new ApmString("glob"),
                new ApmList([new ApmString("read")])
        ])


        when:
        def descriptor = factory.evaluate(mapper, "allow", parameters)

        then:
        descriptor.command == "allow"
        descriptor.argsToString() == "[/path, glob, [read]]"
    }

    def "creates action description - the shortest set of parameters"() {
        given:
        MapperDescriptor mapper = MapperDescriptorFactory.create(AllowMapper.class)
        ActionDescriptionFactory factory = new ActionDescriptionFactory()
        Parameters parameters = new Parameters([
                new ApmString("/path"),
                new ApmList([new ApmString("read")])
        ])


        when:
        def descriptor = factory.evaluate(mapper, "allow", parameters)

        then:
        descriptor.command == "allow"
        descriptor.argsToString() == "[/path, [read]]"
    }

    def "invalid set of parameters"() {
        given:
        MapperDescriptor mapper = MapperDescriptorFactory.create(AllowMapper.class)
        ActionDescriptionFactory factory = new ActionDescriptionFactory()
        Parameters parameters = new Parameters([
                new ApmString("/path"),
                new ApmList([new ApmString("read")]),
                new ApmString("if_exists"),
                new ApmString("if_exists")
        ])


        when:
        factory.evaluate(mapper, "allow", parameters)

        then:
        thrown(ActionCreationException)
    }
}
