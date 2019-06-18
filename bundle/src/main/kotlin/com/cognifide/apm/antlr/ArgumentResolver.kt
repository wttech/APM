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

import com.cognifide.apm.antlr.ApmLangParser.*
import com.google.common.primitives.Ints

class ArgumentResolver(private val variableHolder: VariableHolder) {

    private val resolver: Resolver

    init {
        this.resolver = Resolver()
    }

    fun resolve(context: ArgumentContext): ApmType {
        return resolver.visit(context)
    }

    private inner class Resolver : ApmLangBaseVisitor<ApmType>() {

        override fun defaultResult(): ApmType {
            return ApmEmpty()
        }

        override fun visitArray(ctx: ArrayContext): ApmType {
            val values = ctx.children
                    ?.map { child -> child.accept(this) }
                    ?.filter { child -> child is ApmString }
                    ?.map { child -> child as ApmString }
                    ?: listOf()
            return ApmStringList(values)
        }

        override fun visitExpression(ctx: ExpressionContext): ApmType {
            if (ctx.plus() != null) {
                val leftValue = visit(ctx.expression(0))
                val rightValue = visit(ctx.expression(1))
                return when {
                    leftValue is ApmString && rightValue is ApmString -> ApmString(leftValue.string + rightValue.string)
                    leftValue is ApmString && rightValue is ApmInteger -> ApmString(leftValue.string + rightValue.integer.toString())
                    leftValue is ApmInteger && rightValue is ApmString -> ApmString(leftValue.integer.toString() + rightValue.string)
                    leftValue is ApmInteger && rightValue is ApmInteger -> ApmInteger(leftValue.integer + rightValue.integer)
                    else -> throw ArgumentResolverException("Operation not supported for given values $leftValue and $rightValue")
                }
            }
            return if (ctx.value() != null) {
                visit(ctx.value())
            } else {
                super.visitExpression(ctx)
            }
        }

        override fun visitNumberValue(ctx: NumberValueContext): ApmType {
            val value = ctx.NUMBER_LITERAL().toString()
            val number = Ints.tryParse(value)
                    ?: throw ArgumentResolverException("Found invalid number value $value")
            return ApmInteger(number)
        }

        override fun visitStringValue(ctx: StringValueContext): ApmType {
            if (ctx.STRING_LITERAL() != null) {
                return ApmString(ctx.STRING_LITERAL().toPlainString())
            }
            if (ctx.IDENTIFIER() != null) {
                return ApmString(ctx.IDENTIFIER().toString())
            }
            throw ArgumentResolverException("Found invalid string value $ctx")
        }

        override fun visitVariable(ctx: VariableContext): ApmType {
            val name = ctx.IDENTIFIER().toString()
            return variableHolder.get(name)
                    ?: throw ArgumentResolverException("Variable $name not found")
        }

    }
}
