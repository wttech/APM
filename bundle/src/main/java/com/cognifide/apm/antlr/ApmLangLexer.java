// Generated from ApmLang.g4 by ANTLR 4.7.1
package com.cognifide.apm.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ApmLangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
      T__0 = 1, T__1 = 2, T__2 = 3, ARRAY_BEGIN = 4, ARRAY_END = 5, BLOCK_BEGIN = 6, BLOCK_END = 7,
      DEFINE_MACRO = 8, EXECUTE_MACRO = 9, INCLUDE_SCRIPT = 10, NUMBER_LITERAL = 11,
      STRING_LITERAL = 12, VARIABLE_PREFIX = 13, BOOLEAN_VALUE = 14, NULL_VALUE = 15,
      IDENTIFIER = 16, COMMENT = 17, WHITESPACE = 18, EOL = 19;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
      "T__0", "T__1", "T__2", "ARRAY_BEGIN", "ARRAY_END", "BLOCK_BEGIN", "BLOCK_END",
      "DEFINE_MACRO", "EXECUTE_MACRO", "INCLUDE_SCRIPT", "NUMBER_LITERAL", "STRING_LITERAL",
      "VARIABLE_PREFIX", "BOOLEAN_VALUE", "NULL_VALUE", "IDENTIFIER", "COMMENT",
      "Digits", "LetterOrDigit", "Letter", "WHITESPACE", "EOL"
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


	public ApmLangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ApmLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
      "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\25\u00ed\b\1\4\2" +
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
          "\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\3\3" +
          "\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7" +
          "D\n\7\3\b\3\b\3\b\3\b\3\b\3\b\5\bL\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t" +
          "\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\tf" +
          "\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3" +
          "\n\3\n\5\nz\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13" +
          "\3\13\3\13\3\13\5\13\u008a\n\13\3\f\6\f\u008d\n\f\r\f\16\f\u008e\3\r\3" +
          "\r\7\r\u0093\n\r\f\r\16\r\u0096\13\r\3\r\3\r\3\r\7\r\u009b\n\r\f\r\16" +
          "\r\u009e\13\r\3\r\5\r\u00a1\n\r\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17" +
          "\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u00b7" +
          "\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00c1\n\20\3\21\3\21" +
          "\7\21\u00c5\n\21\f\21\16\21\u00c8\13\21\3\22\3\22\7\22\u00cc\n\22\f\22" +
          "\16\22\u00cf\13\22\3\23\3\23\7\23\u00d3\n\23\f\23\16\23\u00d6\13\23\3" +
          "\23\5\23\u00d9\n\23\3\24\3\24\5\24\u00dd\n\24\3\25\3\25\3\25\3\25\5\25" +
          "\u00e3\n\25\3\26\3\26\3\26\3\26\3\27\3\27\3\27\5\27\u00ec\n\27\2\2\30" +
          "\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20" +
          "\37\21!\22#\23%\2\'\2)\2+\24-\25\3\2\r\3\2\62;\6\2\f\f\17\17$$^^\6\2\f" +
          "\f\17\17))^^\5\2\f\f\17\17^^\4\2\62;aa\5\2C\\aac|\4\2\2\u0081\ud802\udc01" +
          "\3\2\ud802\udc01\3\2\udc02\ue001\4\2\13\13\"\"\4\2\f\f\17\17\2\u00fe\2" +
          "\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2" +
          "\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2" +
          "\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2" +
          "\2\2+\3\2\2\2\2-\3\2\2\2\3/\3\2\2\2\5\61\3\2\2\2\7\63\3\2\2\2\t\65\3\2" +
          "\2\2\13\67\3\2\2\2\rC\3\2\2\2\17K\3\2\2\2\21e\3\2\2\2\23y\3\2\2\2\25\u0089" +
          "\3\2\2\2\27\u008c\3\2\2\2\31\u00a0\3\2\2\2\33\u00a2\3\2\2\2\35\u00b6\3" +
          "\2\2\2\37\u00c0\3\2\2\2!\u00c2\3\2\2\2#\u00c9\3\2\2\2%\u00d0\3\2\2\2\'" +
          "\u00dc\3\2\2\2)\u00e2\3\2\2\2+\u00e4\3\2\2\2-\u00eb\3\2\2\2/\60\7.\2\2" +
          "\60\4\3\2\2\2\61\62\7*\2\2\62\6\3\2\2\2\63\64\7+\2\2\64\b\3\2\2\2\65\66" +
          "\7]\2\2\66\n\3\2\2\2\678\7_\2\28\f\3\2\2\29:\7d\2\2:;\7g\2\2;<\7i\2\2" +
          "<=\7k\2\2=D\7p\2\2>?\7D\2\2?@\7G\2\2@A\7I\2\2AB\7K\2\2BD\7P\2\2C9\3\2" +
          "\2\2C>\3\2\2\2D\16\3\2\2\2EF\7g\2\2FG\7p\2\2GL\7f\2\2HI\7G\2\2IJ\7P\2" +
          "\2JL\7F\2\2KE\3\2\2\2KH\3\2\2\2L\20\3\2\2\2MN\7f\2\2NO\7g\2\2OP\7h\2\2" +
          "PQ\7k\2\2QR\7p\2\2RS\7g\2\2ST\7\"\2\2TU\7o\2\2UV\7c\2\2VW\7e\2\2WX\7t" +
          "\2\2Xf\7q\2\2YZ\7F\2\2Z[\7G\2\2[\\\7H\2\2\\]\7K\2\2]^\7P\2\2^_\7G\2\2" +
          "_`\7\"\2\2`a\7O\2\2ab\7C\2\2bc\7E\2\2cd\7T\2\2df\7Q\2\2eM\3\2\2\2eY\3" +
          "\2\2\2f\22\3\2\2\2gh\7w\2\2hi\7u\2\2ij\7g\2\2jk\7\"\2\2kl\7o\2\2lm\7c" +
          "\2\2mn\7e\2\2no\7t\2\2oz\7q\2\2pq\7W\2\2qr\7U\2\2rs\7G\2\2st\7\"\2\2t" +
          "u\7O\2\2uv\7C\2\2vw\7E\2\2wx\7T\2\2xz\7Q\2\2yg\3\2\2\2yp\3\2\2\2z\24\3" +
          "\2\2\2{|\7k\2\2|}\7p\2\2}~\7e\2\2~\177\7n\2\2\177\u0080\7w\2\2\u0080\u0081" +
          "\7f\2\2\u0081\u008a\7g\2\2\u0082\u0083\7K\2\2\u0083\u0084\7P\2\2\u0084" +
          "\u0085\7E\2\2\u0085\u0086\7N\2\2\u0086\u0087\7W\2\2\u0087\u0088\7F\2\2" +
          "\u0088\u008a\7G\2\2\u0089{\3\2\2\2\u0089\u0082\3\2\2\2\u008a\26\3\2\2" +
          "\2\u008b\u008d\t\2\2\2\u008c\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008c" +
          "\3\2\2\2\u008e\u008f\3\2\2\2\u008f\30\3\2\2\2\u0090\u0094\7$\2\2\u0091" +
          "\u0093\n\3\2\2\u0092\u0091\3\2\2\2\u0093\u0096\3\2\2\2\u0094\u0092\3\2" +
          "\2\2\u0094\u0095\3\2\2\2\u0095\u0097\3\2\2\2\u0096\u0094\3\2\2\2\u0097" +
          "\u00a1\7$\2\2\u0098\u009c\7)\2\2\u0099\u009b\n\4\2\2\u009a\u0099\3\2\2" +
          "\2\u009b\u009e\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009f" +
          "\3\2\2\2\u009e\u009c\3\2\2\2\u009f\u00a1\7)\2\2\u00a0\u0090\3\2\2\2\u00a0" +
          "\u0098\3\2\2\2\u00a1\32\3\2\2\2\u00a2\u00a3\7&\2\2\u00a3\34\3\2\2\2\u00a4" +
          "\u00a5\7v\2\2\u00a5\u00a6\7t\2\2\u00a6\u00a7\7w\2\2\u00a7\u00b7\7g\2\2" +
          "\u00a8\u00a9\7V\2\2\u00a9\u00aa\7T\2\2\u00aa\u00ab\7W\2\2\u00ab\u00b7" +
          "\7G\2\2\u00ac\u00ad\7h\2\2\u00ad\u00ae\7c\2\2\u00ae\u00af\7n\2\2\u00af" +
          "\u00b0\7u\2\2\u00b0\u00b7\7g\2\2\u00b1\u00b2\7H\2\2\u00b2\u00b3\7C\2\2" +
          "\u00b3\u00b4\7N\2\2\u00b4\u00b5\7U\2\2\u00b5\u00b7\7G\2\2\u00b6\u00a4" +
          "\3\2\2\2\u00b6\u00a8\3\2\2\2\u00b6\u00ac\3\2\2\2\u00b6\u00b1\3\2\2\2\u00b7" +
          "\36\3\2\2\2\u00b8\u00b9\7p\2\2\u00b9\u00ba\7w\2\2\u00ba\u00bb\7n\2\2\u00bb" +
          "\u00c1\7n\2\2\u00bc\u00bd\7P\2\2\u00bd\u00be\7W\2\2\u00be\u00bf\7N\2\2" +
          "\u00bf\u00c1\7N\2\2\u00c0\u00b8\3\2\2\2\u00c0\u00bc\3\2\2\2\u00c1 \3\2" +
          "\2\2\u00c2\u00c6\5)\25\2\u00c3\u00c5\5\'\24\2\u00c4\u00c3\3\2\2\2\u00c5" +
          "\u00c8\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\"\3\2\2\2" +
          "\u00c8\u00c6\3\2\2\2\u00c9\u00cd\7%\2\2\u00ca\u00cc\n\5\2\2\u00cb\u00ca" +
          "\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce" +
          "$\3\2\2\2\u00cf\u00cd\3\2\2\2\u00d0\u00d8\t\2\2\2\u00d1\u00d3\t\6\2\2" +
          "\u00d2\u00d1\3\2\2\2\u00d3\u00d6\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5" +
          "\3\2\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d7\u00d9\t\2\2\2\u00d8" +
          "\u00d4\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9&\3\2\2\2\u00da\u00dd\5)\25\2" +
          "\u00db\u00dd\t\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00db\3\2\2\2\u00dd(\3" +
          "\2\2\2\u00de\u00e3\t\7\2\2\u00df\u00e3\n\b\2\2\u00e0\u00e1\t\t\2\2\u00e1" +
          "\u00e3\t\n\2\2\u00e2\u00de\3\2\2\2\u00e2\u00df\3\2\2\2\u00e2\u00e0\3\2" +
          "\2\2\u00e3*\3\2\2\2\u00e4\u00e5\t\13\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e7" +
          "\b\26\2\2\u00e7,\3\2\2\2\u00e8\u00e9\7\17\2\2\u00e9\u00ec\7\f\2\2\u00ea" +
          "\u00ec\t\f\2\2\u00eb\u00e8\3\2\2\2\u00eb\u00ea\3\2\2\2\u00ec.\3\2\2\2" +
          "\25\2CKey\u0089\u008e\u0094\u009c\u00a0\u00b6\u00c0\u00c6\u00cd\u00d4" +
          "\u00d8\u00dc\u00e2\u00eb\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}