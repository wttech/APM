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
                    ?.map { child -> child.string }
                    ?: listOf()
            return ApmList(values)
        }

        override fun visitExpression(ctx: ExpressionContext): ApmType {
            if (ctx.plus() != null) {
                val left = visit(ctx.expression(0))
                val right = visit(ctx.expression(1))
                return when {
                    left is ApmString && right is ApmString -> ApmString(left.string + right.string)
                    left is ApmString && right is ApmInteger -> ApmString(left.string + right.integer.toString())
                    left is ApmInteger && right is ApmString -> ApmString(left.integer.toString() + right.string)
                    left is ApmInteger && right is ApmInteger -> ApmInteger(left.integer + right.integer)
                    left is ApmList && right is ApmList -> ApmList(left.list + right.list)
                    else -> throw ArgumentResolverException("Operation not supported for given values $left and $right")
                }
            }
            return when {
                ctx.value() != null -> visit(ctx.value())
                ctx.array() != null -> visit(ctx.array())
                else -> super.visitExpression(ctx)
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
