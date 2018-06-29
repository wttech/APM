// Generated from ApmLang.g4 by ANTLR 4.7.1
package com.cognifide.apm.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ApmLangParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, ALLOW=6, BEGIN=7, END=8, DEFINE_MACRO=9, 
		USE_MACRO=10, STRING_LITERAL=11, IDENTIFIER=12, COMMENT=13, WHITESPACE=14, 
		EOL=15;
	public static final int
		RULE_apm = 0, RULE_name = 1, RULE_variable = 2, RULE_parameter = 3, RULE_comment = 4, 
		RULE_command = 5, RULE_parametersDefinition = 6, RULE_parametersInvokation = 7, 
		RULE_body = 8, RULE_macroDefinition = 9;
	public static final String[] ruleNames = {
		"apm", "name", "variable", "parameter", "comment", "command", "parametersDefinition", 
		"parametersInvokation", "body", "macroDefinition"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'${'", "'}'", "'('", "','", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "ALLOW", "BEGIN", "END", "DEFINE_MACRO", 
		"USE_MACRO", "STRING_LITERAL", "IDENTIFIER", "COMMENT", "WHITESPACE", 
		"EOL"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ApmLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ApmLangParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ApmContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(ApmLangParser.EOF, 0); }
		public List<CommandContext> command() {
			return getRuleContexts(CommandContext.class);
		}
		public CommandContext command(int i) {
			return getRuleContext(CommandContext.class,i);
		}
		public List<MacroDefinitionContext> macroDefinition() {
			return getRuleContexts(MacroDefinitionContext.class);
		}
		public MacroDefinitionContext macroDefinition(int i) {
			return getRuleContext(MacroDefinitionContext.class,i);
		}
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public List<TerminalNode> EOL() { return getTokens(ApmLangParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(ApmLangParser.EOL, i);
		}
		public ApmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_apm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterApm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitApm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitApm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ApmContext apm() throws RecognitionException {
		ApmContext _localctx = new ApmContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_apm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(24);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ALLOW:
				case USE_MACRO:
				case IDENTIFIER:
					{
					setState(20);
					command();
					}
					break;
				case DEFINE_MACRO:
					{
					setState(21);
					macroDefinition();
					}
					break;
				case COMMENT:
					{
					setState(22);
					comment();
					}
					break;
				case EOL:
					{
					setState(23);
					match(EOL);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(26); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ALLOW) | (1L << DEFINE_MACRO) | (1L << USE_MACRO) | (1L << IDENTIFIER) | (1L << COMMENT) | (1L << EOL))) != 0) );
			setState(28);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(ApmLangParser.IDENTIFIER, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(ApmLangParser.IDENTIFIER, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(T__0);
			setState(33);
			match(IDENTIFIER);
			setState(34);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterContext extends ParserRuleContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(ApmLangParser.IDENTIFIER, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(ApmLangParser.STRING_LITERAL, 0); }
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_parameter);
		try {
			setState(39);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(36);
				variable();
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(37);
				match(IDENTIFIER);
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(38);
				match(STRING_LITERAL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(ApmLangParser.COMMENT, 0); }
		public TerminalNode EOL() { return getToken(ApmLangParser.EOL, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(COMMENT);
			setState(42);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandContext extends ParserRuleContext {
		public CommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command; }
	 
		public CommandContext() { }
		public void copyFrom(CommandContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CommandAllowContext extends CommandContext {
		public TerminalNode ALLOW() { return getToken(ApmLangParser.ALLOW, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public TerminalNode EOL() { return getToken(ApmLangParser.EOL, 0); }
		public CommandAllowContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterCommandAllow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitCommandAllow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitCommandAllow(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CommandUseMacroContext extends CommandContext {
		public TerminalNode USE_MACRO() { return getToken(ApmLangParser.USE_MACRO, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode EOL() { return getToken(ApmLangParser.EOL, 0); }
		public ParametersInvokationContext parametersInvokation() {
			return getRuleContext(ParametersInvokationContext.class,0);
		}
		public CommandUseMacroContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterCommandUseMacro(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitCommandUseMacro(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitCommandUseMacro(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CommandGenericContext extends CommandContext {
		public TerminalNode IDENTIFIER() { return getToken(ApmLangParser.IDENTIFIER, 0); }
		public TerminalNode EOL() { return getToken(ApmLangParser.EOL, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public CommandGenericContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterCommandGeneric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitCommandGeneric(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitCommandGeneric(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_command);
		int _la;
		try {
			setState(66);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case USE_MACRO:
				_localctx = new CommandUseMacroContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(44);
				match(USE_MACRO);
				setState(45);
				name();
				setState(47);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(46);
					parametersInvokation();
					}
				}

				setState(49);
				match(EOL);
				}
				break;
			case ALLOW:
				_localctx = new CommandAllowContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(51);
				match(ALLOW);
				setState(52);
				parameter();
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << STRING_LITERAL) | (1L << IDENTIFIER))) != 0)) {
					{
					setState(53);
					parameter();
					}
				}

				setState(56);
				match(EOL);
				}
				break;
			case IDENTIFIER:
				_localctx = new CommandGenericContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(58);
				match(IDENTIFIER);
				setState(60); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(59);
					parameter();
					}
					}
					setState(62); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << STRING_LITERAL) | (1L << IDENTIFIER))) != 0) );
				setState(64);
				match(EOL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParametersDefinitionContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(ApmLangParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(ApmLangParser.IDENTIFIER, i);
		}
		public ParametersDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parametersDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterParametersDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitParametersDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitParametersDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametersDefinitionContext parametersDefinition() throws RecognitionException {
		ParametersDefinitionContext _localctx = new ParametersDefinitionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_parametersDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(T__2);
			setState(69);
			match(IDENTIFIER);
			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(70);
				match(T__3);
				setState(71);
				match(IDENTIFIER);
				}
				}
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(77);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParametersInvokationContext extends ParserRuleContext {
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public ParametersInvokationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parametersInvokation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterParametersInvokation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitParametersInvokation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitParametersInvokation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametersInvokationContext parametersInvokation() throws RecognitionException {
		ParametersInvokationContext _localctx = new ParametersInvokationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_parametersInvokation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			match(T__2);
			setState(80);
			parameter();
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(81);
				match(T__3);
				setState(82);
				parameter();
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(88);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BodyContext extends ParserRuleContext {
		public List<CommandContext> command() {
			return getRuleContexts(CommandContext.class);
		}
		public CommandContext command(int i) {
			return getRuleContext(CommandContext.class,i);
		}
		public BodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BodyContext body() throws RecognitionException {
		BodyContext _localctx = new BodyContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(90);
				command();
				}
				}
				setState(93); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ALLOW) | (1L << USE_MACRO) | (1L << IDENTIFIER))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MacroDefinitionContext extends ParserRuleContext {
		public TerminalNode DEFINE_MACRO() { return getToken(ApmLangParser.DEFINE_MACRO, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode BEGIN() { return getToken(ApmLangParser.BEGIN, 0); }
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public TerminalNode END() { return getToken(ApmLangParser.END, 0); }
		public List<TerminalNode> EOL() { return getTokens(ApmLangParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(ApmLangParser.EOL, i);
		}
		public ParametersDefinitionContext parametersDefinition() {
			return getRuleContext(ParametersDefinitionContext.class,0);
		}
		public MacroDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macroDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).enterMacroDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ApmLangListener ) ((ApmLangListener)listener).exitMacroDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ApmLangVisitor ) return ((ApmLangVisitor<? extends T>)visitor).visitMacroDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MacroDefinitionContext macroDefinition() throws RecognitionException {
		MacroDefinitionContext _localctx = new MacroDefinitionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_macroDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(DEFINE_MACRO);
			setState(96);
			name();
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(97);
				parametersDefinition();
				}
			}

			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOL) {
				{
				setState(100);
				match(EOL);
				}
			}

			setState(103);
			match(BEGIN);
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOL) {
				{
				setState(104);
				match(EOL);
				}
			}

			setState(107);
			body();
			setState(108);
			match(END);
			setState(109);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\21r\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\3"+
		"\2\3\2\3\2\3\2\6\2\33\n\2\r\2\16\2\34\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4"+
		"\3\5\3\5\3\5\5\5*\n\5\3\6\3\6\3\6\3\7\3\7\3\7\5\7\62\n\7\3\7\3\7\3\7\3"+
		"\7\3\7\5\79\n\7\3\7\3\7\3\7\3\7\6\7?\n\7\r\7\16\7@\3\7\3\7\5\7E\n\7\3"+
		"\b\3\b\3\b\3\b\7\bK\n\b\f\b\16\bN\13\b\3\b\3\b\3\t\3\t\3\t\3\t\7\tV\n"+
		"\t\f\t\16\tY\13\t\3\t\3\t\3\n\6\n^\n\n\r\n\16\n_\3\13\3\13\3\13\5\13e"+
		"\n\13\3\13\5\13h\n\13\3\13\3\13\5\13l\n\13\3\13\3\13\3\13\3\13\3\13\2"+
		"\2\f\2\4\6\b\n\f\16\20\22\24\2\2\2x\2\32\3\2\2\2\4 \3\2\2\2\6\"\3\2\2"+
		"\2\b)\3\2\2\2\n+\3\2\2\2\fD\3\2\2\2\16F\3\2\2\2\20Q\3\2\2\2\22]\3\2\2"+
		"\2\24a\3\2\2\2\26\33\5\f\7\2\27\33\5\24\13\2\30\33\5\n\6\2\31\33\7\21"+
		"\2\2\32\26\3\2\2\2\32\27\3\2\2\2\32\30\3\2\2\2\32\31\3\2\2\2\33\34\3\2"+
		"\2\2\34\32\3\2\2\2\34\35\3\2\2\2\35\36\3\2\2\2\36\37\7\2\2\3\37\3\3\2"+
		"\2\2 !\7\16\2\2!\5\3\2\2\2\"#\7\3\2\2#$\7\16\2\2$%\7\4\2\2%\7\3\2\2\2"+
		"&*\5\6\4\2\'*\7\16\2\2(*\7\r\2\2)&\3\2\2\2)\'\3\2\2\2)(\3\2\2\2*\t\3\2"+
		"\2\2+,\7\17\2\2,-\7\21\2\2-\13\3\2\2\2./\7\f\2\2/\61\5\4\3\2\60\62\5\20"+
		"\t\2\61\60\3\2\2\2\61\62\3\2\2\2\62\63\3\2\2\2\63\64\7\21\2\2\64E\3\2"+
		"\2\2\65\66\7\b\2\2\668\5\b\5\2\679\5\b\5\28\67\3\2\2\289\3\2\2\29:\3\2"+
		"\2\2:;\7\21\2\2;E\3\2\2\2<>\7\16\2\2=?\5\b\5\2>=\3\2\2\2?@\3\2\2\2@>\3"+
		"\2\2\2@A\3\2\2\2AB\3\2\2\2BC\7\21\2\2CE\3\2\2\2D.\3\2\2\2D\65\3\2\2\2"+
		"D<\3\2\2\2E\r\3\2\2\2FG\7\5\2\2GL\7\16\2\2HI\7\6\2\2IK\7\16\2\2JH\3\2"+
		"\2\2KN\3\2\2\2LJ\3\2\2\2LM\3\2\2\2MO\3\2\2\2NL\3\2\2\2OP\7\7\2\2P\17\3"+
		"\2\2\2QR\7\5\2\2RW\5\b\5\2ST\7\6\2\2TV\5\b\5\2US\3\2\2\2VY\3\2\2\2WU\3"+
		"\2\2\2WX\3\2\2\2XZ\3\2\2\2YW\3\2\2\2Z[\7\7\2\2[\21\3\2\2\2\\^\5\f\7\2"+
		"]\\\3\2\2\2^_\3\2\2\2_]\3\2\2\2_`\3\2\2\2`\23\3\2\2\2ab\7\13\2\2bd\5\4"+
		"\3\2ce\5\16\b\2dc\3\2\2\2de\3\2\2\2eg\3\2\2\2fh\7\21\2\2gf\3\2\2\2gh\3"+
		"\2\2\2hi\3\2\2\2ik\7\t\2\2jl\7\21\2\2kj\3\2\2\2kl\3\2\2\2lm\3\2\2\2mn"+
		"\5\22\n\2no\7\n\2\2op\7\21\2\2p\25\3\2\2\2\17\32\34)\618@DLW_dgk";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}