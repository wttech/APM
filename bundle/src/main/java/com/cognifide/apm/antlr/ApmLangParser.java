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
      T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, ARRAY_BEGIN = 6, ARRAY_END = 7, BLOCK_BEGIN = 8,
      BLOCK_END = 9, DEFINE_MACRO = 10, EXECUTE_MACRO = 11, INCLUDE_SCRIPT = 12, STRING_LITERAL = 13,
      IDENTIFIER = 14, COMMENT = 15, WHITESPACE = 16, EOL = 17;
	public static final int
      RULE_apm = 0, RULE_line = 1, RULE_name = 2, RULE_array = 3, RULE_value = 4,
      RULE_variable = 5, RULE_parameter = 6, RULE_comment = 7, RULE_command = 8,
      RULE_parametersDefinition = 9, RULE_parametersInvokation = 10, RULE_body = 11,
      RULE_scriptInclusion = 12, RULE_macroDefinition = 13;
	public static final String[] ruleNames = {
      "apm", "line", "name", "array", "value", "variable", "parameter", "comment",
      "command", "parametersDefinition", "parametersInvokation", "body", "scriptInclusion",
      "macroDefinition"
	};

	private static final String[] _LITERAL_NAMES = {
      null, "','", "'${'", "'}'", "'('", "')'", "'['", "']'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
      null, null, null, null, null, null, "ARRAY_BEGIN", "ARRAY_END", "BLOCK_BEGIN",
      "BLOCK_END", "DEFINE_MACRO", "EXECUTE_MACRO", "INCLUDE_SCRIPT", "STRING_LITERAL",
      "IDENTIFIER", "COMMENT", "WHITESPACE", "EOL"
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
        setState(32);
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
            case 1: {
              {
                setState(29);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 &&
                    ((1L << _la) & ((1L << DEFINE_MACRO) | (1L << EXECUTE_MACRO) | (1L << INCLUDE_SCRIPT) | (1L
                        << IDENTIFIER) | (1L << COMMENT))) != 0)) {
                  {
                    setState(28);
                    line();
                  }
                }

                setState(31);
                match(EOL);
              }
            }
            break;
            default:
              throw new NoViableAltException(this);
          }
          setState(34);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
        setState(37);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 &&
            ((1L << _la) & ((1L << DEFINE_MACRO) | (1L << EXECUTE_MACRO) | (1L << INCLUDE_SCRIPT) | (1L << IDENTIFIER)
                | (1L << COMMENT))) != 0)) {
          {
            setState(36);
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

    public ScriptInclusionContext scriptInclusion() {
      return getRuleContext(ScriptInclusionContext.class, 0);
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
        setState(43);
        _errHandler.sync(this);
        switch (_input.LA(1)) {
          case EXECUTE_MACRO:
          case IDENTIFIER: {
            setState(39);
            command();
          }
          break;
          case DEFINE_MACRO: {
            setState(40);
            macroDefinition();
          }
          break;
          case INCLUDE_SCRIPT: {
            setState(41);
            scriptInclusion();
          }
          break;
          case COMMENT: {
            setState(42);
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
        setState(45);
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

  public static class ArrayContext extends ParserRuleContext {

    public TerminalNode ARRAY_BEGIN() {
      return getToken(ApmLangParser.ARRAY_BEGIN, 0);
    }

    public List<ValueContext> value() {
      return getRuleContexts(ValueContext.class);
    }

    public ValueContext value(int i) {
      return getRuleContext(ValueContext.class, i);
    }

    public TerminalNode ARRAY_END() {
      return getToken(ApmLangParser.ARRAY_END, 0);
    }

    public ArrayContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_array;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterArray(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitArray(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitArray(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final ArrayContext array() throws RecognitionException {
    ArrayContext _localctx = new ArrayContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_array);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(47);
        match(ARRAY_BEGIN);
        setState(48);
        value();
        setState(53);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == T__0) {
          {
            {
              setState(49);
              match(T__0);
              setState(50);
              value();
            }
          }
          setState(55);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(56);
        match(ARRAY_END);
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

  public static class ValueContext extends ParserRuleContext {

    public VariableContext variable() {
      return getRuleContext(VariableContext.class, 0);
    }

    public TerminalNode IDENTIFIER() {
      return getToken(ApmLangParser.IDENTIFIER, 0);
    }

    public TerminalNode STRING_LITERAL() {
      return getToken(ApmLangParser.STRING_LITERAL, 0);
    }

    public ValueContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_value;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterValue(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitValue(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitValue(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final ValueContext value() throws RecognitionException {
    ValueContext _localctx = new ValueContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_value);
    try {
      setState(61);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case T__1:
          enterOuterAlt(_localctx, 1);
        {
          setState(58);
          variable();
        }
        break;
        case IDENTIFIER:
          enterOuterAlt(_localctx, 2);
        {
          setState(59);
          match(IDENTIFIER);
        }
        break;
        case STRING_LITERAL:
          enterOuterAlt(_localctx, 3);
        {
          setState(60);
          match(STRING_LITERAL);
        }
        break;
        default:
          throw new NoViableAltException(this);
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
    enterRule(_localctx, 10, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(63);
        match(T__1);
        setState(64);
			match(IDENTIFIER);
        setState(65);
        match(T__2);
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

    public ArrayContext array() {
      return getRuleContext(ArrayContext.class, 0);
    }
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
    enterRule(_localctx, 12, RULE_parameter);
		try {
      setState(71);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
        case ARRAY_BEGIN:
				enterOuterAlt(_localctx, 1);
				{
          setState(67);
          array();
        }
        break;
        case T__1:
          enterOuterAlt(_localctx, 2);
        {
          setState(68);
				variable();
				}
				break;
			case IDENTIFIER:
        enterOuterAlt(_localctx, 3);
				{
          setState(69);
				match(IDENTIFIER);
				}
				break;
			case STRING_LITERAL:
        enterOuterAlt(_localctx, 4);
				{
          setState(70);
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
    enterRule(_localctx, 14, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(73);
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

  public static class MacroExecutionContext extends CommandContext {

    public TerminalNode EXECUTE_MACRO() {
      return getToken(ApmLangParser.EXECUTE_MACRO, 0);
    }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ParametersInvokationContext parametersInvokation() {
			return getRuleContext(ParametersInvokationContext.class,0);
		}

    public MacroExecutionContext(CommandContext ctx) {
      copyFrom(ctx);
    }
		@Override
		public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterMacroExecution(this);
      }
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitMacroExecution(this);
      }
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitMacroExecution(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public static class GenericCommandContext extends CommandContext {
		public TerminalNode IDENTIFIER() { return getToken(ApmLangParser.IDENTIFIER, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}

    public GenericCommandContext(CommandContext ctx) {
      copyFrom(ctx);
    }
		@Override
		public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterGenericCommand(this);
      }
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitGenericCommand(this);
      }
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitGenericCommand(this);
      } else {
        return visitor.visitChildren(this);
      }
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_command);
		int _la;
		try {
      setState(86);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
        case EXECUTE_MACRO:
          _localctx = new MacroExecutionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
          setState(75);
          match(EXECUTE_MACRO);
          setState(76);
				name();
          setState(78);
				_errHandler.sync(this);
				_la = _input.LA(1);
          if (_la == T__3) {
					{
            setState(77);
					parametersInvokation();
					}
				}

				}
				break;
			case IDENTIFIER:
        _localctx = new GenericCommandContext(_localctx);
        enterOuterAlt(_localctx, 2);
				{
          setState(80);
				match(IDENTIFIER);
          setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
            setState(81);
            parameter();
          }
          }
          setState(84);
					_errHandler.sync(this);
					_la = _input.LA(1);
        } while ((((_la) & ~0x3f) == 0
            && ((1L << _la) & ((1L << T__1) | (1L << ARRAY_BEGIN) | (1L << STRING_LITERAL) | (1L << IDENTIFIER)))
            != 0));
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
    enterRule(_localctx, 18, RULE_parametersDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(88);
        match(T__3);
        setState(89);
			match(IDENTIFIER);
        setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
        while (_la == T__0) {
				{
				{
          setState(90);
          match(T__0);
          setState(91);
				match(IDENTIFIER);
				}
				}
          setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
        setState(97);
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
    enterRule(_localctx, 20, RULE_parametersInvokation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(99);
        match(T__3);
        setState(100);
			parameter();
        setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
        while (_la == T__0) {
				{
				{
          setState(101);
          match(T__0);
          setState(102);
				parameter();
				}
				}
          setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
        setState(108);
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
    enterRule(_localctx, 22, RULE_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(114);
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
            {
              setState(111);
              _errHandler.sync(this);
              _la = _input.LA(1);
              if (_la == EXECUTE_MACRO || _la == IDENTIFIER) {
                {
                  setState(110);
                  command();
                }
				}

              setState(113);
              match(EOL);
            }
          }
          setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
        } while ((((_la) & ~0x3f) == 0
            && ((1L << _la) & ((1L << EXECUTE_MACRO) | (1L << IDENTIFIER) | (1L << EOL))) != 0));
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

  public static class ScriptInclusionContext extends ParserRuleContext {

    public TerminalNode INCLUDE_SCRIPT() {
      return getToken(ApmLangParser.INCLUDE_SCRIPT, 0);
    }

    public ParameterContext parameter() {
      return getRuleContext(ParameterContext.class, 0);
    }

    public ScriptInclusionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_scriptInclusion;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterScriptInclusion(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitScriptInclusion(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitScriptInclusion(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final ScriptInclusionContext scriptInclusion() throws RecognitionException {
    ScriptInclusionContext _localctx = new ScriptInclusionContext(_ctx, getState());
    enterRule(_localctx, 24, RULE_scriptInclusion);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(118);
        match(INCLUDE_SCRIPT);
        setState(119);
        parameter();
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

    public TerminalNode BLOCK_BEGIN() {
      return getToken(ApmLangParser.BLOCK_BEGIN, 0);
    }
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}

    public TerminalNode BLOCK_END() {
      return getToken(ApmLangParser.BLOCK_END, 0);
    }

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
    enterRule(_localctx, 26, RULE_macroDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(121);
			match(DEFINE_MACRO);
        setState(122);
			name();
        setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
        if (_la == T__3) {
				{
          setState(123);
				parametersDefinition();
				}
			}

        setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOL) {
				{
          setState(126);
				match(EOL);
				}
			}

        setState(129);
        match(BLOCK_BEGIN);
        setState(131);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 16, _ctx)) {
          case 1: {
            setState(130);
            match(EOL);
          }
          break;
        }
        setState(133);
        body();
        setState(134);
        match(BLOCK_END);
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
      "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\23\u008b\4\2\t\2" +
          "\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
          "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\5\2 \n\2\3\2\6\2#\n\2\r" +
          "\2\16\2$\3\2\5\2(\n\2\3\3\3\3\3\3\3\3\5\3.\n\3\3\4\3\4\3\5\3\5\3\5\3\5" +
          "\7\5\66\n\5\f\5\16\59\13\5\3\5\3\5\3\6\3\6\3\6\5\6@\n\6\3\7\3\7\3\7\3" +
          "\7\3\b\3\b\3\b\3\b\5\bJ\n\b\3\t\3\t\3\n\3\n\3\n\5\nQ\n\n\3\n\3\n\6\nU" +
          "\n\n\r\n\16\nV\5\nY\n\n\3\13\3\13\3\13\3\13\7\13_\n\13\f\13\16\13b\13" +
          "\13\3\13\3\13\3\f\3\f\3\f\3\f\7\fj\n\f\f\f\16\fm\13\f\3\f\3\f\3\r\5\r" +
          "r\n\r\3\r\6\ru\n\r\r\r\16\rv\3\16\3\16\3\16\3\17\3\17\3\17\5\17\177\n" +
          "\17\3\17\5\17\u0082\n\17\3\17\3\17\5\17\u0086\n\17\3\17\3\17\3\17\3\17" +
          "\2\2\20\2\4\6\b\n\f\16\20\22\24\26\30\32\34\2\2\2\u0092\2\"\3\2\2\2\4" +
          "-\3\2\2\2\6/\3\2\2\2\b\61\3\2\2\2\n?\3\2\2\2\fA\3\2\2\2\16I\3\2\2\2\20" +
          "K\3\2\2\2\22X\3\2\2\2\24Z\3\2\2\2\26e\3\2\2\2\30t\3\2\2\2\32x\3\2\2\2" +
          "\34{\3\2\2\2\36 \5\4\3\2\37\36\3\2\2\2\37 \3\2\2\2 !\3\2\2\2!#\7\23\2" +
          "\2\"\37\3\2\2\2#$\3\2\2\2$\"\3\2\2\2$%\3\2\2\2%\'\3\2\2\2&(\5\4\3\2\'" +
          "&\3\2\2\2\'(\3\2\2\2(\3\3\2\2\2).\5\22\n\2*.\5\34\17\2+.\5\32\16\2,.\5" +
          "\20\t\2-)\3\2\2\2-*\3\2\2\2-+\3\2\2\2-,\3\2\2\2.\5\3\2\2\2/\60\7\20\2" +
          "\2\60\7\3\2\2\2\61\62\7\b\2\2\62\67\5\n\6\2\63\64\7\3\2\2\64\66\5\n\6" +
          "\2\65\63\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678\3\2\2\28:\3\2\2\29\67\3" +
          "\2\2\2:;\7\t\2\2;\t\3\2\2\2<@\5\f\7\2=@\7\20\2\2>@\7\17\2\2?<\3\2\2\2" +
          "?=\3\2\2\2?>\3\2\2\2@\13\3\2\2\2AB\7\4\2\2BC\7\20\2\2CD\7\5\2\2D\r\3\2" +
          "\2\2EJ\5\b\5\2FJ\5\f\7\2GJ\7\20\2\2HJ\7\17\2\2IE\3\2\2\2IF\3\2\2\2IG\3" +
          "\2\2\2IH\3\2\2\2J\17\3\2\2\2KL\7\21\2\2L\21\3\2\2\2MN\7\r\2\2NP\5\6\4" +
          "\2OQ\5\26\f\2PO\3\2\2\2PQ\3\2\2\2QY\3\2\2\2RT\7\20\2\2SU\5\16\b\2TS\3" +
          "\2\2\2UV\3\2\2\2VT\3\2\2\2VW\3\2\2\2WY\3\2\2\2XM\3\2\2\2XR\3\2\2\2Y\23" +
          "\3\2\2\2Z[\7\6\2\2[`\7\20\2\2\\]\7\3\2\2]_\7\20\2\2^\\\3\2\2\2_b\3\2\2" +
          "\2`^\3\2\2\2`a\3\2\2\2ac\3\2\2\2b`\3\2\2\2cd\7\7\2\2d\25\3\2\2\2ef\7\6" +
          "\2\2fk\5\16\b\2gh\7\3\2\2hj\5\16\b\2ig\3\2\2\2jm\3\2\2\2ki\3\2\2\2kl\3" +
          "\2\2\2ln\3\2\2\2mk\3\2\2\2no\7\7\2\2o\27\3\2\2\2pr\5\22\n\2qp\3\2\2\2" +
          "qr\3\2\2\2rs\3\2\2\2su\7\23\2\2tq\3\2\2\2uv\3\2\2\2vt\3\2\2\2vw\3\2\2" +
          "\2w\31\3\2\2\2xy\7\16\2\2yz\5\16\b\2z\33\3\2\2\2{|\7\f\2\2|~\5\6\4\2}" +
          "\177\5\24\13\2~}\3\2\2\2~\177\3\2\2\2\177\u0081\3\2\2\2\u0080\u0082\7" +
          "\23\2\2\u0081\u0080\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0083\3\2\2\2\u0083" +
          "\u0085\7\n\2\2\u0084\u0086\7\23\2\2\u0085\u0084\3\2\2\2\u0085\u0086\3" +
          "\2\2\2\u0086\u0087\3\2\2\2\u0087\u0088\5\30\r\2\u0088\u0089\7\13\2\2\u0089" +
          "\35\3\2\2\2\23\37$\'-\67?IPVX`kqv~\u0081\u0085";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}