// Generated from ApmLang.g4 by ANTLR 4.7.1
package com.cognifide.apm.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ApmLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ApmLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#apm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitApm(ApmLangParser.ApmContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(ApmLangParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(ApmLangParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(ApmLangParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(ApmLangParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#booleanValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanValue(ApmLangParser.BooleanValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#nullValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullValue(ApmLangParser.NullValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#numberValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberValue(ApmLangParser.NumberValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#stringValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringValue(ApmLangParser.StringValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#stringConst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringConst(ApmLangParser.StringConstContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(ApmLangParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(ApmLangParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#variableDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDefinition(ApmLangParser.VariableDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(ApmLangParser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MacroExecution}
	 * labeled alternative in {@link ApmLangParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacroExecution(ApmLangParser.MacroExecutionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GenericCommand}
	 * labeled alternative in {@link ApmLangParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericCommand(ApmLangParser.GenericCommandContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#parametersDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParametersDefinition(ApmLangParser.ParametersDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#parametersInvocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParametersInvocation(ApmLangParser.ParametersInvocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(ApmLangParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPath(ApmLangParser.PathContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#scriptInclusion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScriptInclusion(ApmLangParser.ScriptInclusionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ApmLangParser#macroDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacroDefinition(ApmLangParser.MacroDefinitionContext ctx);
}