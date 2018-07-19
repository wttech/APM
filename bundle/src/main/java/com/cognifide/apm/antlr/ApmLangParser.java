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
      T__0 = 1, T__1 = 2, T__2 = 3, ARRAY_BEGIN = 4, ARRAY_END = 5, BLOCK_BEGIN = 6, BLOCK_END = 7,
      DEFINE_MACRO = 8, EXECUTE_MACRO = 9, INCLUDE_SCRIPT = 10, NUMBER_LITERAL = 11,
      STRING_LITERAL = 12, VARIABLE_PREFIX = 13, BOOLEAN_VALUE = 14, NULL_VALUE = 15,
      IDENTIFIER = 16, COMMENT = 17, WHITESPACE = 18, EOL = 19;
	public static final int
      RULE_apm = 0, RULE_line = 1, RULE_name = 2, RULE_array = 3, RULE_variable = 4,
      RULE_booleanValue = 5, RULE_nullValue = 6, RULE_numberValue = 7, RULE_stringValue = 8,
      RULE_stringConst = 9, RULE_value = 10, RULE_parameter = 11, RULE_comment = 12,
      RULE_command = 13, RULE_parametersDefinition = 14, RULE_parametersInvocation = 15,
      RULE_body = 16, RULE_path = 17, RULE_scriptInclusion = 18, RULE_macroDefinition = 19;
	public static final String[] ruleNames = {
      "apm", "line", "name", "array", "variable", "booleanValue", "nullValue",
      "numberValue", "stringValue", "stringConst", "value", "parameter", "comment",
      "command", "parametersDefinition", "parametersInvocation", "body", "path",
      "scriptInclusion", "macroDefinition"
	};

	private static final String[] _LITERAL_NAMES = {
      null, "','", "'('", "')'", "'['", "']'", null, null, null, null, null,
      null, null, "'$'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
      null, null, null, null, "ARRAY_BEGIN", "ARRAY_END", "BLOCK_BEGIN", "BLOCK_END",
      "DEFINE_MACRO", "EXECUTE_MACRO", "INCLUDE_SCRIPT", "NUMBER_LITERAL", "STRING_LITERAL",
      "VARIABLE_PREFIX", "BOOLEAN_VALUE", "NULL_VALUE", "IDENTIFIER", "COMMENT",
      "WHITESPACE", "EOL"
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
        setState(44);
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
            case 1: {
              {
                setState(41);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 &&
                    ((1L << _la) & ((1L << DEFINE_MACRO) | (1L << EXECUTE_MACRO) | (1L << INCLUDE_SCRIPT) | (1L
                        << IDENTIFIER) | (1L << COMMENT))) != 0)) {
                  {
                    setState(40);
                    line();
                  }
                }

                setState(43);
                match(EOL);
              }
            }
            break;
            default:
              throw new NoViableAltException(this);
          }
          setState(46);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
        setState(49);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 &&
            ((1L << _la) & ((1L << DEFINE_MACRO) | (1L << EXECUTE_MACRO) | (1L << INCLUDE_SCRIPT) | (1L << IDENTIFIER)
                | (1L << COMMENT))) != 0)) {
          {
            setState(48);
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
        setState(55);
        _errHandler.sync(this);
        switch (_input.LA(1)) {
          case EXECUTE_MACRO:
          case IDENTIFIER: {
            setState(51);
            command();
          }
          break;
          case DEFINE_MACRO: {
            setState(52);
            macroDefinition();
          }
          break;
          case INCLUDE_SCRIPT: {
            setState(53);
            scriptInclusion();
          }
          break;
          case COMMENT: {
            setState(54);
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
        setState(57);
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
        setState(59);
        match(ARRAY_BEGIN);
        setState(60);
        value();
        setState(65);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == T__0) {
          {
            {
              setState(61);
              match(T__0);
              setState(62);
              value();
            }
          }
          setState(67);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(68);
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

  public static class VariableContext extends ParserRuleContext {

    public TerminalNode VARIABLE_PREFIX() {
      return getToken(ApmLangParser.VARIABLE_PREFIX, 0);
    }

    public TerminalNode IDENTIFIER() {
      return getToken(ApmLangParser.IDENTIFIER, 0);
    }

    public VariableContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_variable;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterVariable(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitVariable(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitVariable(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final VariableContext variable() throws RecognitionException {
    VariableContext _localctx = new VariableContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_variable);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(70);
        match(VARIABLE_PREFIX);
        setState(71);
        match(IDENTIFIER);
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

  public static class BooleanValueContext extends ParserRuleContext {

    public TerminalNode BOOLEAN_VALUE() {
      return getToken(ApmLangParser.BOOLEAN_VALUE, 0);
    }

    public BooleanValueContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_booleanValue;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterBooleanValue(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitBooleanValue(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitBooleanValue(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final BooleanValueContext booleanValue() throws RecognitionException {
    BooleanValueContext _localctx = new BooleanValueContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_booleanValue);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(73);
        match(BOOLEAN_VALUE);
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

  public static class NullValueContext extends ParserRuleContext {

    public TerminalNode NULL_VALUE() {
      return getToken(ApmLangParser.NULL_VALUE, 0);
    }

    public NullValueContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_nullValue;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterNullValue(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitNullValue(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitNullValue(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final NullValueContext nullValue() throws RecognitionException {
    NullValueContext _localctx = new NullValueContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_nullValue);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(75);
        match(NULL_VALUE);
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

  public static class NumberValueContext extends ParserRuleContext {

    public TerminalNode NUMBER_LITERAL() {
      return getToken(ApmLangParser.NUMBER_LITERAL, 0);
    }

    public NumberValueContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_numberValue;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterNumberValue(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitNumberValue(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitNumberValue(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final NumberValueContext numberValue() throws RecognitionException {
    NumberValueContext _localctx = new NumberValueContext(_ctx, getState());
    enterRule(_localctx, 14, RULE_numberValue);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(77);
        match(NUMBER_LITERAL);
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

  public static class StringValueContext extends ParserRuleContext {

    public TerminalNode STRING_LITERAL() {
      return getToken(ApmLangParser.STRING_LITERAL, 0);
    }

    public StringValueContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_stringValue;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterStringValue(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitStringValue(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitStringValue(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final StringValueContext stringValue() throws RecognitionException {
    StringValueContext _localctx = new StringValueContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_stringValue);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(79);
        match(STRING_LITERAL);
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

  public static class StringConstContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(ApmLangParser.IDENTIFIER, 0); }

    public StringConstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

    @Override
    public int getRuleIndex() {
      return RULE_stringConst;
    }
		@Override
		public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterStringConst(this);
      }
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitStringConst(this);
      }
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitStringConst(this);
      } else {
        return visitor.visitChildren(this);
      }
		}
	}

  public final StringConstContext stringConst() throws RecognitionException {
    StringConstContext _localctx = new StringConstContext(_ctx, getState());
    enterRule(_localctx, 18, RULE_stringConst);
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(81);
			match(IDENTIFIER);
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

    public BooleanValueContext booleanValue() {
      return getRuleContext(BooleanValueContext.class, 0);
    }

    public NullValueContext nullValue() {
      return getRuleContext(NullValueContext.class, 0);
    }

    public NumberValueContext numberValue() {
      return getRuleContext(NumberValueContext.class, 0);
    }

    public StringValueContext stringValue() {
      return getRuleContext(StringValueContext.class, 0);
    }

    public StringConstContext stringConst() {
      return getRuleContext(StringConstContext.class, 0);
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
    enterRule(_localctx, 20, RULE_value);
    try {
      setState(89);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case VARIABLE_PREFIX:
          enterOuterAlt(_localctx, 1);
        {
          setState(83);
          variable();
        }
        break;
        case BOOLEAN_VALUE:
          enterOuterAlt(_localctx, 2);
        {
          setState(84);
          booleanValue();
        }
        break;
        case NULL_VALUE:
          enterOuterAlt(_localctx, 3);
        {
          setState(85);
          nullValue();
        }
        break;
        case NUMBER_LITERAL:
          enterOuterAlt(_localctx, 4);
        {
          setState(86);
          numberValue();
        }
        break;
        case STRING_LITERAL:
          enterOuterAlt(_localctx, 5);
        {
          setState(87);
          stringValue();
        }
        break;
        case IDENTIFIER:
          enterOuterAlt(_localctx, 6);
        {
          setState(88);
          stringConst();
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

	public static class ParameterContext extends ParserRuleContext {

    public ArrayContext array() {
      return getRuleContext(ArrayContext.class, 0);
    }

    public ValueContext value() {
      return getRuleContext(ValueContext.class, 0);
		}
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
    enterRule(_localctx, 22, RULE_parameter);
		try {
      setState(93);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
        case ARRAY_BEGIN:
				enterOuterAlt(_localctx, 1);
				{
          setState(91);
          array();
        }
        break;
        case NUMBER_LITERAL:
        case STRING_LITERAL:
        case VARIABLE_PREFIX:
        case BOOLEAN_VALUE:
        case NULL_VALUE:
        case IDENTIFIER:
          enterOuterAlt(_localctx, 2);
        {
          setState(92);
          value();
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
    enterRule(_localctx, 24, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(95);
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

    public ParametersInvocationContext parametersInvocation() {
      return getRuleContext(ParametersInvocationContext.class, 0);
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
    enterRule(_localctx, 26, RULE_command);
		int _la;
		try {
      setState(108);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
        case EXECUTE_MACRO:
          _localctx = new MacroExecutionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
          setState(97);
          match(EXECUTE_MACRO);
          setState(98);
				name();
          setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
          if (_la == T__1) {
					{
            setState(99);
            parametersInvocation();
					}
				}

				}
				break;
			case IDENTIFIER:
        _localctx = new GenericCommandContext(_localctx);
        enterOuterAlt(_localctx, 2);
				{
          setState(102);
          match(IDENTIFIER);
          setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
            setState(103);
            parameter();
          }
          }
          setState(106);
					_errHandler.sync(this);
					_la = _input.LA(1);
        } while ((((_la) & ~0x3f) == 0 &&
            ((1L << _la) & ((1L << ARRAY_BEGIN) | (1L << NUMBER_LITERAL) | (1L << STRING_LITERAL) | (1L
                << VARIABLE_PREFIX) | (1L << BOOLEAN_VALUE) | (1L << NULL_VALUE) | (1L << IDENTIFIER))) != 0));
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
    enterRule(_localctx, 28, RULE_parametersDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(110);
        match(T__1);
        setState(111);
			match(IDENTIFIER);
        setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
        while (_la == T__0) {
				{
				{
          setState(112);
          match(T__0);
          setState(113);
				match(IDENTIFIER);
				}
				}
          setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
        setState(119);
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

  public static class ParametersInvocationContext extends ParserRuleContext {
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}

    public ParametersInvocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

    @Override
    public int getRuleIndex() {
      return RULE_parametersInvocation;
    }
		@Override
		public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterParametersInvocation(this);
      }
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitParametersInvocation(this);
      }
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitParametersInvocation(this);
      } else {
        return visitor.visitChildren(this);
      }
		}
	}

  public final ParametersInvocationContext parametersInvocation() throws RecognitionException {
    ParametersInvocationContext _localctx = new ParametersInvocationContext(_ctx, getState());
    enterRule(_localctx, 30, RULE_parametersInvocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(121);
        match(T__1);
        setState(122);
			parameter();
        setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
        while (_la == T__0) {
				{
				{
          setState(123);
          match(T__0);
          setState(124);
				parameter();
				}
				}
          setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
        setState(130);
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
    enterRule(_localctx, 32, RULE_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(136);
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
            {
              setState(133);
              _errHandler.sync(this);
              _la = _input.LA(1);
              if (_la == EXECUTE_MACRO || _la == IDENTIFIER) {
                {
                  setState(132);
                  command();
                }
				}

              setState(135);
              match(EOL);
            }
          }
          setState(138);
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

  public static class PathContext extends ParserRuleContext {

    public TerminalNode STRING_LITERAL() {
      return getToken(ApmLangParser.STRING_LITERAL, 0);
    }

    public PathContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_path;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).enterPath(this);
      }
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof ApmLangListener) {
        ((ApmLangListener) listener).exitPath(this);
      }
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof ApmLangVisitor) {
        return ((ApmLangVisitor<? extends T>) visitor).visitPath(this);
      } else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final PathContext path() throws RecognitionException {
    PathContext _localctx = new PathContext(_ctx, getState());
    enterRule(_localctx, 34, RULE_path);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(140);
        match(STRING_LITERAL);
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

    public PathContext path() {
      return getRuleContext(PathContext.class, 0);
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
    enterRule(_localctx, 36, RULE_scriptInclusion);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(142);
        match(INCLUDE_SCRIPT);
        setState(143);
        path();
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
    enterRule(_localctx, 38, RULE_macroDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
        setState(145);
			match(DEFINE_MACRO);
        setState(146);
			name();
        setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
        if (_la == T__1) {
				{
          setState(147);
          parametersDefinition();
        }
        }

        setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOL) {
				{
          setState(150);
				match(EOL);
				}
			}

        setState(153);
        match(BLOCK_BEGIN);
        setState(155);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 16, _ctx)) {
          case 1: {
            setState(154);
            match(EOL);
          }
          break;
        }
        setState(157);
        body();
        setState(158);
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
      "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\25\u00a3\4\2\t\2" +
          "\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
          "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
          "\4\23\t\23\4\24\t\24\4\25\t\25\3\2\5\2,\n\2\3\2\6\2/\n\2\r\2\16\2\60\3" +
          "\2\5\2\64\n\2\3\3\3\3\3\3\3\3\5\3:\n\3\3\4\3\4\3\5\3\5\3\5\3\5\7\5B\n" +
          "\5\f\5\16\5E\13\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n" +
          "\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\\\n\f\3\r\3\r\5\r`\n\r\3\16\3\16" +
          "\3\17\3\17\3\17\5\17g\n\17\3\17\3\17\6\17k\n\17\r\17\16\17l\5\17o\n\17" +
          "\3\20\3\20\3\20\3\20\7\20u\n\20\f\20\16\20x\13\20\3\20\3\20\3\21\3\21" +
          "\3\21\3\21\7\21\u0080\n\21\f\21\16\21\u0083\13\21\3\21\3\21\3\22\5\22" +
          "\u0088\n\22\3\22\6\22\u008b\n\22\r\22\16\22\u008c\3\23\3\23\3\24\3\24" +
          "\3\24\3\25\3\25\3\25\5\25\u0097\n\25\3\25\5\25\u009a\n\25\3\25\3\25\5" +
          "\25\u009e\n\25\3\25\3\25\3\25\3\25\2\2\26\2\4\6\b\n\f\16\20\22\24\26\30" +
          "\32\34\36 \"$&(\2\2\2\u00a5\2.\3\2\2\2\49\3\2\2\2\6;\3\2\2\2\b=\3\2\2" +
          "\2\nH\3\2\2\2\fK\3\2\2\2\16M\3\2\2\2\20O\3\2\2\2\22Q\3\2\2\2\24S\3\2\2" +
          "\2\26[\3\2\2\2\30_\3\2\2\2\32a\3\2\2\2\34n\3\2\2\2\36p\3\2\2\2 {\3\2\2" +
          "\2\"\u008a\3\2\2\2$\u008e\3\2\2\2&\u0090\3\2\2\2(\u0093\3\2\2\2*,\5\4" +
          "\3\2+*\3\2\2\2+,\3\2\2\2,-\3\2\2\2-/\7\25\2\2.+\3\2\2\2/\60\3\2\2\2\60" +
          ".\3\2\2\2\60\61\3\2\2\2\61\63\3\2\2\2\62\64\5\4\3\2\63\62\3\2\2\2\63\64" +
          "\3\2\2\2\64\3\3\2\2\2\65:\5\34\17\2\66:\5(\25\2\67:\5&\24\28:\5\32\16" +
          "\29\65\3\2\2\29\66\3\2\2\29\67\3\2\2\298\3\2\2\2:\5\3\2\2\2;<\7\22\2\2" +
          "<\7\3\2\2\2=>\7\6\2\2>C\5\26\f\2?@\7\3\2\2@B\5\26\f\2A?\3\2\2\2BE\3\2" +
          "\2\2CA\3\2\2\2CD\3\2\2\2DF\3\2\2\2EC\3\2\2\2FG\7\7\2\2G\t\3\2\2\2HI\7" +
          "\17\2\2IJ\7\22\2\2J\13\3\2\2\2KL\7\20\2\2L\r\3\2\2\2MN\7\21\2\2N\17\3" +
          "\2\2\2OP\7\r\2\2P\21\3\2\2\2QR\7\16\2\2R\23\3\2\2\2ST\7\22\2\2T\25\3\2" +
          "\2\2U\\\5\n\6\2V\\\5\f\7\2W\\\5\16\b\2X\\\5\20\t\2Y\\\5\22\n\2Z\\\5\24" +
          "\13\2[U\3\2\2\2[V\3\2\2\2[W\3\2\2\2[X\3\2\2\2[Y\3\2\2\2[Z\3\2\2\2\\\27" +
          "\3\2\2\2]`\5\b\5\2^`\5\26\f\2_]\3\2\2\2_^\3\2\2\2`\31\3\2\2\2ab\7\23\2" +
          "\2b\33\3\2\2\2cd\7\13\2\2df\5\6\4\2eg\5 \21\2fe\3\2\2\2fg\3\2\2\2go\3" +
          "\2\2\2hj\7\22\2\2ik\5\30\r\2ji\3\2\2\2kl\3\2\2\2lj\3\2\2\2lm\3\2\2\2m" +
          "o\3\2\2\2nc\3\2\2\2nh\3\2\2\2o\35\3\2\2\2pq\7\4\2\2qv\7\22\2\2rs\7\3\2" +
          "\2su\7\22\2\2tr\3\2\2\2ux\3\2\2\2vt\3\2\2\2vw\3\2\2\2wy\3\2\2\2xv\3\2" +
          "\2\2yz\7\5\2\2z\37\3\2\2\2{|\7\4\2\2|\u0081\5\30\r\2}~\7\3\2\2~\u0080" +
          "\5\30\r\2\177}\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082" +
          "\3\2\2\2\u0082\u0084\3\2\2\2\u0083\u0081\3\2\2\2\u0084\u0085\7\5\2\2\u0085" +
          "!\3\2\2\2\u0086\u0088\5\34\17\2\u0087\u0086\3\2\2\2\u0087\u0088\3\2\2" +
          "\2\u0088\u0089\3\2\2\2\u0089\u008b\7\25\2\2\u008a\u0087\3\2\2\2\u008b" +
          "\u008c\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d#\3\2\2\2" +
          "\u008e\u008f\7\16\2\2\u008f%\3\2\2\2\u0090\u0091\7\f\2\2\u0091\u0092\5" +
          "$\23\2\u0092\'\3\2\2\2\u0093\u0094\7\n\2\2\u0094\u0096\5\6\4\2\u0095\u0097" +
          "\5\36\20\2\u0096\u0095\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0099\3\2\2\2" +
          "\u0098\u009a\7\25\2\2\u0099\u0098\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009b" +
          "\3\2\2\2\u009b\u009d\7\b\2\2\u009c\u009e\7\25\2\2\u009d\u009c\3\2\2\2" +
          "\u009d\u009e\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\5\"\22\2\u00a0\u00a1" +
          "\7\t\2\2\u00a1)\3\2\2\2\23+\60\639C[_flnv\u0081\u0087\u008c\u0096\u0099" +
          "\u009d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}