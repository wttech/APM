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

import com.cognifide.apm.core.crypto.DecryptionService
import com.cognifide.apm.core.grammar.ApmType.ApmInteger
import com.cognifide.apm.core.grammar.ApmType.ApmList
import com.cognifide.apm.core.grammar.ApmType.ApmString
import com.cognifide.apm.core.grammar.argument.ArgumentResolver
import com.cognifide.apm.core.grammar.argument.ArgumentResolverException
import com.cognifide.apm.core.grammar.datasource.DataSourceInvoker
import com.cognifide.apm.core.grammar.executioncontext.VariableHolder
import com.google.common.collect.Lists
import org.apache.sling.api.resource.ResourceResolver
import spock.lang.Specification

class ArgumentResolverTest extends Specification {

    private variableHolder = new VariableHolder()

    def "declaring multiline list"() {
        given:
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript(
                """[
                    'a',
                    'b',
                    'c'
                ]""")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getList() == [new ApmString("a"), new ApmString("b"), new ApmString("c")]
    }

    def "declaring multiline structure with encryption"() {
        given:
        def parameterResolver = createArgumentResolver()
        def decryptionService = new DecryptionService() {
            @Override
            protected String unprotect(String text) {
                return text.substring(1, text.length() - 1).reverse()
            }
        }
        def parser = ApmLangParserHelper.createParserUsingScript(
                """{
                        x1: "{tset}",
                        x2: "tset",
                        y: 1,
                        z: [
                            "{renni}", "renni", 2
                        ]
                        }""")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())
        def data = result.required[0].getArgument(decryptionService)

        then:
        data.get("x1") == "test"
        data.get("x2") == "tset"
        data.get("y") == 1
        data.get("z") == ["inner", "renni", 2]
    }

    def "accessing not existing variable"() {
        given:
        variableHolder.set("var1", new ApmString("val1"))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 \$var2")

        when:
        parameterResolver.resolve(parser.complexArguments())

        then:
        def exception = thrown(ArgumentResolverException.class)
        exception.message == "Variable \"var2\" not found"
    }

    def "concatenation of strings"() {
        given:
        variableHolder.set("var1", new ApmString("val1"))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 + 'val2'")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getString() == "val1val2"
    }

    def "concatenation of string and number"() {
        given:
        variableHolder.set("var1", new ApmString("val1"))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 + 10")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getString() == "val110"
    }

    def "sum of lists"() {
        given:
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("['a', 'b'] + ['c', 'd']")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getList() == [new ApmString("a"), new ApmString("b"), new ApmString("c"), new ApmString("d")]
    }

    def "sum of numbers"() {
        given:
        variableHolder.set("var1", new ApmInteger(10))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 + 7")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getInteger() == 17
    }

    def "invalid sum of elements"() {
        given:
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("['a', 'b'] + 5")

        when:
        parameterResolver.resolve(parser.complexArguments())

        then:
        def exception = thrown(ArgumentResolverException.class)
        exception.message == "Operation not supported for given values [\"a\", \"b\"] and 5"
    }

    def "resolve number parameters"() {
        given:
        variableHolder.set("var1", new ApmInteger(1))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 2")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getInteger() == 1
        result.required[1].getInteger() == 2
    }

    def "string substitution"() {
        given:
        variableHolder.set("var1", new ApmString("test"))
        variableHolder.set("var2", new ApmInteger(1))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("'\${var1} \${var2}'")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getString() == "test 1"
    }

    def "path substitution"() {
        given:
        variableHolder.set("var1", new ApmString("content"))
        variableHolder.set("var2", new ApmString("test"))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("/\${var1}/\${var2}")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getString() == "/content/test"
    }

    def "resolve string parameters"() {
        given:
        variableHolder.set("var1", new ApmString("val1"))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 'val2'")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getString() == "val1"
        result.required[1].getString() == "val2"
    }

    def "resolve list parameters"() {
        given:
        variableHolder.set("var1", new ApmList(Lists.newArrayList(new ApmString("val1"))))
        variableHolder.set("var2", new ApmString("val2"))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 [\$var2, 'FALSE']")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.required[0].getList() == [new ApmString("val1")]
        result.required[1].getList() == [new ApmString("val2"), new ApmString("FALSE")]
    }

    def "resolve optional parameters"() {
        given:
        variableHolder.set("var1", new ApmList(Lists.newArrayList(new ApmString("val1"))))
        variableHolder.set("var2", new ApmString("val2"))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 param1=[\$var2, 'FALSE'] param2='STRICT'")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.named["param1"].getList() == [new ApmString("val2"), new ApmString("FALSE")]
        result.named["param2"].getString() == "STRICT"
    }

    def "resolve flags"() {
        given:
        variableHolder.set("var1", new ApmList(Lists.newArrayList(new ApmString("val1"))))
        variableHolder.set("var2", new ApmString("val2"))
        def parameterResolver = createArgumentResolver()
        def parser = ApmLangParserHelper.createParserUsingScript("\$var1 param1=[\$var2, 'FALSE'] --IF-EXISTS --DEEP")

        when:
        def result = parameterResolver.resolve(parser.complexArguments())

        then:
        result.flags == ["IF-EXISTS", "DEEP"]
    }

    def createArgumentResolver() {
        def resourceResolver = Mock(ResourceResolver)
        def dataSourceInvoker = new DataSourceInvoker()
        new ArgumentResolver(variableHolder, resourceResolver, dataSourceInvoker)
    }
}
