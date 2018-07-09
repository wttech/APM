// Generated from ApmLang.g4 by ANTLR 4.7.1
package com.cognifide.apm.antlr;

import java.util.List;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

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
      RULE_apm = 0, RULE_line = 1, RULE_name = 2, RULE_variable = 3, RULE_parameter = 4,
      RULE_comment = 5, RULE_command = 6, RULE_parametersDefinition = 7, RULE_parametersInvokation = 8,
      RULE_body = 9, RULE_macroDefinition = 10;
	public static final String[] ruleNames = {
      "apm", "line", "name", "variable", "parameter", "comment", "command",
      "parametersDefinition", "parametersInvokation", "body", "macroDefinition"
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
		public List<TerminalNode> EOL() { return getTokens(ApmLangParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(ApmLangParser.EOL, i);
		}

    public List<LineContext> line() {
      return getRuleContexts(LineContext.class);
    }

    public LineContext line(int i) {
      return getRuleContext(LineContext.class, i);
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
      int _alt;
			enterOuterAlt(_localctx, 1);
			{
        setState(26);
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
            case 1: {
              {
                setState(23);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 &&
                    ((1L << _la) & ((1L << ALLOW) | (1L << DEFINE_MACRO) | (1L << USE_MACRO) | (1L << IDENTIFIER) | (1L
                        << COMMENT))) != 0)) {
                  {
                    setState(22);
                    line();
                  }
                }

                setState(25);
                match(EOL);
              }
            }
            break;
            default:
              throw new NoViableAltException(this);
          }
          setState(28);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
        setState(31);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 &&
            ((1L << _la) & ((1L << ALLOW) | (1L << DEFINE_MACRO) | (1L << USE_MACRO) | (1L << IDENTIFIER) | (1L
                << COMMENT))) != 0)) {
          {
            setState(30);
            line();
          }
        }

      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class LineContext extends ParserRuleContext {

    public CommandContext command() {
      return getRuleContext(CommandContext.class, 0);
    }

    public MacroDefinitionContext macroDefinition() {
      return getRuleContext(MacroDefinitionContext.class, 0);
    }

    public CommentContext comment() {
      return getRuleContext(CommentContext.class, 0);
    }

    public LineContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_line;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterLine(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitLine(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitLine(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final LineContext line() throws RecognitionException {
    LineContext _localctx = new LineContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_line);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(36);
        _errHandler.sync(this);
        switch (_input.LA(1)) {
          case ALLOW:
          case USE_MACRO:
          case IDENTIFIER: {
            setState(33);
            command();
          }
          break;
          case DEFINE_MACRO: {
            setState(34);
            macroDefinition();
          }
          break;
          case COMMENT: {
            setState(35);
            comment();
          }
          break;
          default:
            throw new NoViableAltException(this);
        }
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
    enterRule(_localctx, 4, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(38);
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
    enterRule(_localctx, 6, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(40);
			match(T__0);
        setState(41);
			match(IDENTIFIER);
        setState(42);
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
    enterRule(_localctx, 8, RULE_parameter);
		try {
      setState(47);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
          setState(44);
				variable();
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
          setState(45);
				match(IDENTIFIER);
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 3);
				{
          setState(46);
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
    enterRule(_localctx, 10, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(49);
			match(COMMENT);
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
    enterRule(_localctx, 12, RULE_command);
		int _la;
		try {
      setState(67);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case USE_MACRO:
				_localctx = new CommandUseMacroContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
          setState(51);
				match(USE_MACRO);
          setState(52);
				name();
          setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
            setState(53);
					parametersInvokation();
					}
				}

				}
				break;
			case ALLOW:
				_localctx = new CommandAllowContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
          setState(56);
				match(ALLOW);
          setState(57);
				parameter();
          setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << STRING_LITERAL) | (1L << IDENTIFIER))) != 0)) {
					{
            setState(58);
					parameter();
					}
				}

				}
				break;
			case IDENTIFIER:
				_localctx = new CommandGenericContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
          setState(61);
				match(IDENTIFIER);
          setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
            setState(62);
            parameter();
          }
          }
          setState(65);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << STRING_LITERAL) | (1L << IDENTIFIER))) != 0) );
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
    enterRule(_localctx, 14, RULE_parametersDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(69);
			match(T__2);
        setState(70);
			match(IDENTIFIER);
        setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
          setState(71);
				match(T__3);
          setState(72);
				match(IDENTIFIER);
				}
				}
        setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
        setState(78);
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
    enterRule(_localctx, 16, RULE_parametersInvokation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
        match(T__2);
        setState(81);
			parameter();
        setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(82);
          match(T__3);
          setState(83);
				parameter();
				}
				}
        setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
        setState(89);
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

    public List<TerminalNode> EOL() {
      return getTokens(ApmLangParser.EOL);
    }

    public TerminalNode EOL(int i) {
      return getToken(ApmLangParser.EOL, i);
    }
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
    enterRule(_localctx, 18, RULE_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(95);
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
            {
              setState(92);
              _errHandler.sync(this);
              _la = _input.LA(1);
              if ((((_la) & ~0x3f) == 0
                  && ((1L << _la) & ((1L << ALLOW) | (1L << USE_MACRO) | (1L << IDENTIFIER))) != 0)) {
                {
                  setState(91);
                  command();
                }
				}

              setState(94);
              match(EOL);
            }
          }
          setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
        } while ((((_la) & ~0x3f) == 0
            && ((1L << _la) & ((1L << ALLOW) | (1L << USE_MACRO) | (1L << IDENTIFIER) | (1L << EOL))) != 0));
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

    public ParametersDefinitionContext parametersDefinition() {
      return getRuleContext(ParametersDefinitionContext.class, 0);
    }
		public List<TerminalNode> EOL() { return getTokens(ApmLangParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(ApmLangParser.EOL, i);
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
    enterRule(_localctx, 20, RULE_macroDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(99);
			match(DEFINE_MACRO);
        setState(100);
			name();
        setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
          setState(101);
				parametersDefinition();
				}
			}

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
        match(BEGIN);
        setState(109);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 15, _ctx)) {
          case 1: {
            setState(108);
            match(EOL);
          }
          break;
        }
        setState(111);
        body();
        setState(112);
        match(END);
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
      "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\21u\4\2\t\2\4\3\t" +
          "\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4" +
          "\f\t\f\3\2\5\2\32\n\2\3\2\6\2\35\n\2\r\2\16\2\36\3\2\5\2\"\n\2\3\3\3\3" +
          "\3\3\5\3\'\n\3\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6\5\6\62\n\6\3\7\3\7" +
          "\3\b\3\b\3\b\5\b9\n\b\3\b\3\b\3\b\5\b>\n\b\3\b\3\b\6\bB\n\b\r\b\16\bC" +
          "\5\bF\n\b\3\t\3\t\3\t\3\t\7\tL\n\t\f\t\16\tO\13\t\3\t\3\t\3\n\3\n\3\n" +
          "\3\n\7\nW\n\n\f\n\16\nZ\13\n\3\n\3\n\3\13\5\13_\n\13\3\13\6\13b\n\13\r" +
          "\13\16\13c\3\f\3\f\3\f\5\fi\n\f\3\f\5\fl\n\f\3\f\3\f\5\fp\n\f\3\f\3\f" +
          "\3\f\3\f\2\2\r\2\4\6\b\n\f\16\20\22\24\26\2\2\2|\2\34\3\2\2\2\4&\3\2\2" +
          "\2\6(\3\2\2\2\b*\3\2\2\2\n\61\3\2\2\2\f\63\3\2\2\2\16E\3\2\2\2\20G\3\2" +
          "\2\2\22R\3\2\2\2\24a\3\2\2\2\26e\3\2\2\2\30\32\5\4\3\2\31\30\3\2\2\2\31" +
          "\32\3\2\2\2\32\33\3\2\2\2\33\35\7\21\2\2\34\31\3\2\2\2\35\36\3\2\2\2\36" +
          "\34\3\2\2\2\36\37\3\2\2\2\37!\3\2\2\2 \"\5\4\3\2! \3\2\2\2!\"\3\2\2\2" +
          "\"\3\3\2\2\2#\'\5\16\b\2$\'\5\26\f\2%\'\5\f\7\2&#\3\2\2\2&$\3\2\2\2&%" +
          "\3\2\2\2\'\5\3\2\2\2()\7\16\2\2)\7\3\2\2\2*+\7\3\2\2+,\7\16\2\2,-\7\4" +
          "\2\2-\t\3\2\2\2.\62\5\b\5\2/\62\7\16\2\2\60\62\7\r\2\2\61.\3\2\2\2\61" +
          "/\3\2\2\2\61\60\3\2\2\2\62\13\3\2\2\2\63\64\7\17\2\2\64\r\3\2\2\2\65\66" +
          "\7\f\2\2\668\5\6\4\2\679\5\22\n\28\67\3\2\2\289\3\2\2\29F\3\2\2\2:;\7" +
          "\b\2\2;=\5\n\6\2<>\5\n\6\2=<\3\2\2\2=>\3\2\2\2>F\3\2\2\2?A\7\16\2\2@B" +
          "\5\n\6\2A@\3\2\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2\2DF\3\2\2\2E\65\3\2\2\2" +
          "E:\3\2\2\2E?\3\2\2\2F\17\3\2\2\2GH\7\5\2\2HM\7\16\2\2IJ\7\6\2\2JL\7\16" +
          "\2\2KI\3\2\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2NP\3\2\2\2OM\3\2\2\2PQ\7\7" +
          "\2\2Q\21\3\2\2\2RS\7\5\2\2SX\5\n\6\2TU\7\6\2\2UW\5\n\6\2VT\3\2\2\2WZ\3" +
          "\2\2\2XV\3\2\2\2XY\3\2\2\2Y[\3\2\2\2ZX\3\2\2\2[\\\7\7\2\2\\\23\3\2\2\2" +
          "]_\5\16\b\2^]\3\2\2\2^_\3\2\2\2_`\3\2\2\2`b\7\21\2\2a^\3\2\2\2bc\3\2\2" +
          "\2ca\3\2\2\2cd\3\2\2\2d\25\3\2\2\2ef\7\13\2\2fh\5\6\4\2gi\5\20\t\2hg\3" +
          "\2\2\2hi\3\2\2\2ik\3\2\2\2jl\7\21\2\2kj\3\2\2\2kl\3\2\2\2lm\3\2\2\2mo" +
          "\7\t\2\2np\7\21\2\2on\3\2\2\2op\3\2\2\2pq\3\2\2\2qr\5\24\13\2rs\7\n\2" +
          "\2s\27\3\2\2\2\22\31\36!&\618=CEMX^chko";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}