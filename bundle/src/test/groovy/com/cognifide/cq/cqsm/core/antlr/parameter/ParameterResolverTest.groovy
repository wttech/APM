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

package com.cognifide.cq.cqsm.core.antlr.parameter

import com.cognifide.apm.antlr.ApmLangParser
import com.cognifide.cq.cqsm.ApmLangParserHelper
import com.cognifide.cq.cqsm.core.antlr.ListBaseVisitor
import com.cognifide.cq.cqsm.core.antlr.VariableHolder
import com.cognifide.cq.cqsm.core.antlr.type.*
import com.google.common.collect.Lists
import spock.lang.Specification

class ParameterResolverTest extends Specification {

    def "resolve parameters from new context"() {
        given:
        def variableHolder = VariableHolder.empty()
        variableHolder.put("var1", new ApmString("val1"))
        variableHolder.createLocalContext()
        variableHolder.put("var1", new ApmString("val3"))
        def parameterResolver = new ParameterResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 'val2'")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getString() == "val3"
        result[1].getString() == "val2"
    }

    def "concatenation of strings"() {
        given:
        def variableHolder = VariableHolder.empty()
        variableHolder.put("var1", new ApmString("val1"))
        def parameterResolver = new ParameterResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 + 'val2'")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getString() == "val1val2"
    }

    def "concatenation of other types"() {
        given:
        def variableHolder = VariableHolder.empty()
        variableHolder.put("var1", new ApmNumber(33L))
        def parameterResolver = new ParameterResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1+22+FALSE")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getString() == "3322false"
    }

    def "resolve boolean parameters"() {
        given:
        def variableHolder = VariableHolder.empty()
        variableHolder.put("var1", new ApmBoolean(true))
        def parameterResolver = new ParameterResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 FALSE")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getBoolean() == Boolean.TRUE
        result[1].getBoolean() == Boolean.FALSE
    }

    def "resolve number parameters"() {
        given:
        def variableHolder = VariableHolder.empty()
        variableHolder.put("var1", new ApmNumber(1))
        def parameterResolver = new ParameterResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 2")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getNumber().intValue() == 1
        result[1].getNumber().intValue() == 2
    }

    def "resolve string parameters"() {
        given:
        def variableHolder = VariableHolder.empty()
        variableHolder.put("var1", new ApmString("val1"))
        def parameterResolver = new ParameterResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 'val2'")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getString() == "val1"
        result[1].getString() == "val2"
    }

    def "resolve list parameters"() {
        given:
        def variableHolder = VariableHolder.empty()
        variableHolder.put("var1", new ApmList(Lists.newArrayList(new ApmString("val1"))))
        variableHolder.put("var2", new ApmString("val2"))
        def parameterResolver = new ParameterResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 [\$var2, FALSE]")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getList().get(0).getString() == "val1"
        result[1].getList().get(0).getString() == "val2"
        result[1].getList().get(1).getBoolean() == Boolean.FALSE
    }

    static class ParameterVisitor extends ListBaseVisitor<ApmType> {

        private final ParameterResolver parameterResolver

        ParameterVisitor(ParameterResolver parameterResolver) {
            this.parameterResolver = parameterResolver
        }

        @Override
        List<ApmType> visitParameter(ApmLangParser.ParameterContext ctx) {
            return Collections.<ApmType> singletonList(parameterResolver.resolve(ctx))
        }
    }
}
