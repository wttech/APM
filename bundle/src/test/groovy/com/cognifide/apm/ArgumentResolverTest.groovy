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

package com.cognifide.apm

import com.cognifide.apm.antlr.*
import com.google.common.collect.Lists
import spock.lang.Specification

class ArgumentResolverTest extends Specification {

    private variableHolder = Mock(VariableHolder.class)

    def "declaring multiline list"() {
        given:
        def parameterResolver = new ArgumentResolver(this.variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript(
                """GENERIC [
                    'a',
                    'b',
                    'c'
                ]""")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getList() == ["a", "b", "c"]
    }

    def "accessing not existing variable"() {
        given:
        variableHolder.get("var1") >> new ApmString("val1")
        def parameterResolver = new ArgumentResolver(this.variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 \$var2")

        when:
        new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        def exception = thrown(ArgumentResolverException.class)
        exception.message == "Variable var2 not found"
    }

    def "concatenation of strings"() {
        given:
        variableHolder.get("var1") >> new ApmString("val1")
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 + 'val2'")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getString() == "val1val2"
    }

    def "concatenation of string and number"() {
        given:
        variableHolder.get("var1") >> new ApmString("val1")
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 + 10")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getString() == "val110"
    }

    def "sum of lists"() {
        given:
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC ['a', 'b'] + ['c', 'd']")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getList() == ["a", "b", "c", "d"]
    }

    def "sum of numbers"() {
        given:
        variableHolder.get("var1") >> new ApmInteger(10)
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 + 7")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getInteger() == 17
    }

    def "invalid sum of elements"() {
        given:
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC ['a', 'b'] + 5")

        when:
        new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        def exception = thrown(ArgumentResolverException.class)
        exception.message == "Operation not supported for given values ApmList(value=[a, b]) and ApmInteger(value=5)"
    }

    def "resolve number parameters"() {
        given:
        variableHolder.get("var1") >> new ApmInteger(1)
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 2")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getInteger() == 1
        result[1].getInteger() == 2
    }

    def "resolve string parameters"() {
        given:
        variableHolder.get("var1") >> new ApmString("val1")
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 'val2'")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getString() == "val1"
        result[1].getString() == "val2"
    }

    def "resolve list parameters"() {
        given:
        variableHolder.get("var1") >> new ApmList(Lists.newArrayList("val1"))
        variableHolder.get("var2") >> new ApmString("val2")
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("GENERIC \$var1 [\$var2, FALSE]")

        when:
        def result = new ParameterVisitor(parameterResolver).visit(parser.apm())

        then:
        result[0].getList() == ["val1"]
        result[1].getList() == ["val2", "FALSE"]
    }

    static class ParameterVisitor extends ListBaseVisitor<ApmType> {

        private final ArgumentResolver parameterResolver

        ParameterVisitor(ArgumentResolver parameterResolver) {
            this.parameterResolver = parameterResolver
        }

        @Override
        List<ApmType> visitArgument(ApmLangParser.ArgumentContext ctx) {
            return Collections.<ApmType> singletonList(parameterResolver.resolve(ctx))
        }
    }
}
