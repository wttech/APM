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

package com.cognifide.cq.cqsm.core.actions

import com.cognifide.cq.cqsm.api.actions.annotations.Mapper
import spock.lang.Specification

class MapperDescriptorFactoryTest extends Specification {

    def "cannot create MapperDescriptor for class without Mapper annotation"() {
        given:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()

        when:
        mapperDescriptorFactory.create(MapperWithoutAnnotation.class)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == "Mapper must be annotated with " + Mapper.class.getName()
    }

    def "creates valid MapperDescriptor for SampleMapper"() {
        given:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()

        when:
        def mapperDescriptor = mapperDescriptorFactory.create(SampleMapper.class)

        then:
        mapperDescriptor.name == "sample"
        mapperDescriptor.mappingDescriptors.size() == 2
    }

    def "executes"() {
        given:
        MapperDescriptorFactory mapperDescriptorFactory = new MapperDescriptorFactory()

        when:
        def mapperDescriptor = mapperDescriptorFactory.create(SampleMapper.class)

        then:
        mapperDescriptor.name == "sample"
        mapperDescriptor.mappingDescriptors.size() == 2
    }
}
