// Generated from ApmLang.g4 by ANTLR 4.7.1
package com.cognifide.apm.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ApmLangParser}.
 */
public interface ApmLangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ApmLangParser#apm}.
	 * @param ctx the parse tree
	 */
	void enterApm(ApmLangParser.ApmContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#apm}.
	 * @param ctx the parse tree
	 */
	void exitApm(ApmLangParser.ApmContext ctx);
	/**
   * Enter a parse tree produced by {@link ApmLangParser#line}.
   * @param ctx the parse tree
   */
  void enterLine(ApmLangParser.LineContext ctx);

  /**
   * Exit a parse tree produced by {@link ApmLangParser#line}.
   *
   * @param ctx the parse tree
   */
  void exitLine(ApmLangParser.LineContext ctx);

  /**
   * Enter a parse tree produced by {@link ApmLangParser#name}.
   *
   * @param ctx the parse tree
   */
  void enterName(ApmLangParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ApmLangParser.NameContext ctx);

	/**
	 * Enter a parse tree produced by {@link ApmLangParser#array}.
	 *
	 * @param ctx the parse tree
	 */
	void enterArray(ApmLangParser.ArrayContext ctx);

	/**
	 * Exit a parse tree produced by {@link ApmLangParser#array}.
	 *
	 * @param ctx the parse tree
	 */
	void exitArray(ApmLangParser.ArrayContext ctx);

	/**
	 * Enter a parse tree produced by {@link ApmLangParser#value}.
	 *
	 * @param ctx the parse tree
	 */
	void enterValue(ApmLangParser.ValueContext ctx);

	/**
	 * Exit a parse tree produced by {@link ApmLangParser#value}.
	 *
	 * @param ctx the parse tree
	 */
	void exitValue(ApmLangParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ApmLangParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(ApmLangParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(ApmLangParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link ApmLangParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(ApmLangParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(ApmLangParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link ApmLangParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(ApmLangParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(ApmLangParser.CommentContext ctx);

	/**
	 * Enter a parse tree produced by the {@code MacroExecution}
	 * labeled alternative in {@link ApmLangParser#command}.
	 * @param ctx the parse tree
	 */
	void enterMacroExecution(ApmLangParser.MacroExecutionContext ctx);

	/**
	 * Exit a parse tree produced by the {@code MacroExecution}
	 * labeled alternative in {@link ApmLangParser#command}.
	 * @param ctx the parse tree
	 */
	void exitMacroExecution(ApmLangParser.MacroExecutionContext ctx);

	/**
	 * Enter a parse tree produced by the {@code GenericCommand}
	 * labeled alternative in {@link ApmLangParser#command}.
	 * @param ctx the parse tree
	 */
	void enterGenericCommand(ApmLangParser.GenericCommandContext ctx);

	/**
	 * Exit a parse tree produced by the {@code GenericCommand}
	 * labeled alternative in {@link ApmLangParser#command}.
	 * @param ctx the parse tree
	 */
	void exitGenericCommand(ApmLangParser.GenericCommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link ApmLangParser#parametersDefinition}.
	 * @param ctx the parse tree
	 */
	void enterParametersDefinition(ApmLangParser.ParametersDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#parametersDefinition}.
	 * @param ctx the parse tree
	 */
	void exitParametersDefinition(ApmLangParser.ParametersDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ApmLangParser#parametersInvokation}.
	 * @param ctx the parse tree
	 */
	void enterParametersInvokation(ApmLangParser.ParametersInvokationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#parametersInvokation}.
	 * @param ctx the parse tree
	 */
	void exitParametersInvokation(ApmLangParser.ParametersInvokationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ApmLangParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(ApmLangParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(ApmLangParser.BodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link ApmLangParser#scriptInclusion}.
	 *
	 * @param ctx the parse tree
	 */
	void enterScriptInclusion(ApmLangParser.ScriptInclusionContext ctx);

	/**
	 * Exit a parse tree produced by {@link ApmLangParser#scriptInclusion}.
	 *
	 * @param ctx the parse tree
	 */
	void exitScriptInclusion(ApmLangParser.ScriptInclusionContext ctx);

	/**
	 * Enter a parse tree produced by {@link ApmLangParser#macroDefinition}.
	 * @param ctx the parse tree
	 */
	void enterMacroDefinition(ApmLangParser.MacroDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ApmLangParser#macroDefinition}.
	 * @param ctx the parse tree
	 */
	void exitMacroDefinition(ApmLangParser.MacroDefinitionContext ctx);
}