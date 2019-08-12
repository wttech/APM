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

import com.cognifide.apm.antlr.argument.ArgumentResolver
import com.cognifide.apm.antlr.argument.ArgumentResolverException
import com.cognifide.apm.antlr.executioncontext.VariableHolder
import com.google.common.collect.Lists
import spock.lang.Specification

class ArgumentResolverTest extends Specification {

    private variableHolder = new VariableHolder()

    def "declaring multiline list"() {
        given:
        def parameterResolver = new ArgumentResolver(this.variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript(
                """[
                    'a',
                    'b',
                    'c'
                ]""")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.required[0].getList() == ["a", "b", "c"]
    }

    def "accessing not existing variable"() {
        given:
        variableHolder.set("var1", new ApmString("val1"))
        def parameterResolver = new ArgumentResolver(this.variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 \$var2")

        when:
        parameterResolver.resolve(parser.arguments())

        then:
        def exception = thrown(ArgumentResolverException.class)
        exception.message == "Variable var2 not found"
    }

    def "concatenation of strings"() {
        given:
        variableHolder.set("var1", new ApmString("val1"))
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 + 'val2'")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.required[0].getString() == "val1val2"
    }

    def "concatenation of string and number"() {
        given:
        variableHolder.set("var1", new ApmString("val1"))
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 + 10")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.required[0].getString() == "val110"
    }

    def "sum of lists"() {
        given:
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("['a', 'b'] + ['c', 'd']")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.required[0].getList() == ["a", "b", "c", "d"]
    }

    def "sum of numbers"() {
        given:
        variableHolder.set("var1", new ApmInteger(10))
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 + 7")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.required[0].getInteger() == 17
    }

    def "invalid sum of elements"() {
        given:
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("['a', 'b'] + 5")

        when:
        parameterResolver.resolve(parser.arguments())

        then:
        def exception = thrown(ArgumentResolverException.class)
        exception.message == "Operation not supported for given values [\"a\", \"b\"] and 5"
    }

    def "resolve number parameters"() {
        given:
        variableHolder.set("var1", new ApmInteger(1))
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 2")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.required[0].getInteger() == 1
        result.required[1].getInteger() == 2
    }

    def "resolve string parameters"() {
        given:
        variableHolder.set("var1", new ApmString("val1"))
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 'val2'")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.required[0].getString() == "val1"
        result.required[1].getString() == "val2"
    }

    def "resolve list parameters"() {
        given:
        variableHolder.set("var1", new ApmList(Lists.newArrayList("val1")))
        variableHolder.set("var2", new ApmString("val2"))
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 [\$var2, FALSE]")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.required[0].getList() == ["val1"]
        result.required[1].getList() == ["val2", "FALSE"]
    }

    def "resolve optional parameters"() {
        given:
        variableHolder.set("var1", new ApmList(Lists.newArrayList("val1")))
        variableHolder.set("var2", new ApmString("val2"))
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 param1=[\$var2, FALSE] param2='STRICT'")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.named["param1"].getList() == ["val2", "FALSE"]
        result.named["param2"].getString() == "STRICT"
    }

    def "resolve flags"() {
        given:
        variableHolder.set("var1", new ApmList(Lists.newArrayList("val1")))
        variableHolder.set("var2", new ApmString("val2"))
        def parameterResolver = new ArgumentResolver(variableHolder)
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 param1=[\$var2, FALSE] --IF-EXISTS --DEEP")

        when:
        def result = parameterResolver.resolve(parser.arguments())

        then:
        result.flags == ["IF-EXISTS", "DEEP"]
    }
}
