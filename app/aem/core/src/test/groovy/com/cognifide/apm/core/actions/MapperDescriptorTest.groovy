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

package com.cognifide.apm.core.actions

import com.cognifide.apm.api.actions.Action
import com.cognifide.apm.api.actions.annotations.Mapper
import com.cognifide.apm.api.exceptions.InvalidActionMapperException
import com.cognifide.apm.core.grammar.ApmList
import com.cognifide.apm.core.grammar.ApmString
import com.cognifide.apm.core.grammar.ApmType
import com.cognifide.apm.core.grammar.argument.Arguments
import spock.lang.Specification
import spock.lang.Unroll

class MapperDescriptorTest extends Specification {

    def "cannot create MapperDescriptor for class without Mapper annotation"() {
        given:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()

        when:
        mapperDescriptorFactory.create(MapperWithoutAnnotation.class)

        then:
        def e = thrown(InvalidActionMapperException.class)
        e.message == "Mapper must be annotated with " + Mapper.class.getName()
    }

    def "cannot create MapperDescriptor for method with wrong return type"() {
        given:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()

        when:
        mapperDescriptorFactory.create(MapperWithWrongReturnType.class)

        then:
        def e = thrown(InvalidActionMapperException.class)
        e.message == "Mapping method must have return type " + Action.class.getName()
    }

    def "creates valid MapperDescriptor for SampleMapper"() {
        given:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()

        when:
        def mapperDescriptor = mapperDescriptorFactory.create(SampleMapper.class)

        then:
        mapperDescriptor.name == "sample"
        mapperDescriptor.mappingDescriptors.size() == 4
    }

    @Unroll
    def "checks if SampleMapper handles arguments required= #required, named= #named and flags= #flags"(List<ApmType> required, Map<String, ApmType> named, List<String> flags, boolean result) {
        expect:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()
        def mapperDescriptor = mapperDescriptorFactory.create(SampleMapper.class)
        mapperDescriptor.handles(toArguments(required, named, flags)) == result

        where:
        required                         | named         | flags         || result
        ["/content", ["read", "delete"]] | ["glob": "*"] | ["IF-EXISTS"] || true
        ["/content", ["read", "delete"]] | ["glob": "*"] | []            || true
        ["/content", "read"]             | ["glob": "*"] | ["IF-EXISTS"] || true
        [["read", "delete"]]             | ["glob": "*"] | ["IF-EXISTS"] || false // invalid required
        ["a", "b", "c"]                  | ["glob": "*"] | ["IF-EXISTS"] || false // invalid required
        []                               | ["glob": "*"] | ["IF-EXISTS"] || false // invalid required
        ["/content", ["read", "delete"]] | ["dada": "*"] | ["IF-EXISTS"] || false // invalid named
    }

    @Unroll
    def "SampleMapper returns ActionDescription for arguments required= #required, named= #named and flags= #flags"(List<ApmType> required, Map<String, ApmType> named, List<String> flags, String result) {
        expect:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()
        def mapperDescriptor = mapperDescriptorFactory.create(SampleMapper.class)
        mapperDescriptor.handle(toArguments(required, named, flags)).name == result

        where:
        required                         | named         | flags         || result
        ["/content", ["read", "delete"]] | ["glob": "*"] | ["IF-EXISTS"] || "create2"
        ["/content", ["read", "delete"]] | ["glob": "*"] | []            || "create2"
        ["/content", "read"]             | ["glob": "*"] | ["IF-EXISTS"] || "create1"
    }

    @Unroll
    def "SampleMapper annotated by @Flag and @Flags for flags= #flags"(List<String> flags, String result) {
        expect:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()
        def mapperDescriptor = mapperDescriptorFactory.create(SampleMapper.class)
        mapperDescriptor.handle(toArguments([], [:], flags)).name == result

        where:
        flags                 || result
        ["IF-EXISTS", "FLAG"] || "isFlag-true"
        []                    || "isFlag-false"
        ["IF-EXISTS"]         || "isFlag-false"
    }

    @Unroll
    def "SampleMapper annotated by multiple @Flag for flags= #flags"(List<String> flags, String result) {
        expect:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()
        def mapperDescriptor = mapperDescriptorFactory.create(SampleMapper.class)
        mapperDescriptor.handle(toArguments(["/content"], [:], flags)).name == result

        where:
        flags                 || result
        ["IF-EXISTS", "FLAG"] || "flags-IF-EXISTS-FLAG"
        []                    || "flags-"
        ["IF-EXISTS"]         || "flags-IF-EXISTS"
    }

    def toArguments(List<Object> required, Map<String, Object> named, List<String> flags) {
        def newRequired = required.collect { toApmType(it) }
        def newNamed = named.collectEntries { [it.key, toApmType(it.value)] }
        return new Arguments(newRequired, newNamed, flags)
    }

    def toApmType(Object object) {
        if (object instanceof String) {
            return new ApmString(object)
        }
        if (object instanceof List) {
            return new ApmList(object)
        }
        return null
    }
}
