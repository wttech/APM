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
      T__0 = 1, ARRAY_BEGIN = 2, ARRAY_END = 3, BLOCK_BEGIN = 4, BLOCK_END = 5, DEFINE_MACRO = 6,
      EXECUTE_MACRO = 7, IMPORT_SCRIPT = 8, FOR_EACH = 9, IN = 10, DEFINE = 11, NUMBER_LITERAL = 12,
      STRING_LITERAL = 13, VARIABLE_PREFIX = 14, BOOLEAN_VALUE = 15, NULL_VALUE = 16,
      IDENTIFIER = 17, COMMENT = 18, WHITESPACE = 19, EOL = 20;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
      "T__0", "ARRAY_BEGIN", "ARRAY_END", "BLOCK_BEGIN", "BLOCK_END", "DEFINE_MACRO",
      "EXECUTE_MACRO", "IMPORT_SCRIPT", "FOR_EACH", "IN", "DEFINE", "NUMBER_LITERAL",
      "STRING_LITERAL", "VARIABLE_PREFIX", "BOOLEAN_VALUE", "NULL_VALUE", "IDENTIFIER",
      "COMMENT", "Digits", "LetterOrDigit", "Letter", "WHITESPACE", "EOL"
	};

	private static final String[] _LITERAL_NAMES = {
      null, "','", "'['", "']'", null, null, null, null, null, null, null, null,
      null, null, "'$'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
      null, null, "ARRAY_BEGIN", "ARRAY_END", "BLOCK_BEGIN", "BLOCK_END", "DEFINE_MACRO",
      "EXECUTE_MACRO", "IMPORT_SCRIPT", "FOR_EACH", "IN", "DEFINE", "NUMBER_LITERAL",
      "STRING_LITERAL", "VARIABLE_PREFIX", "BOOLEAN_VALUE", "NULL_VALUE", "IDENTIFIER",
      "COMMENT", "WHITESPACE", "EOL"
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
      "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\26\u010d\b\1\4\2" +
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
          "\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2" +
          "\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5B\n\5" +
          "\3\6\3\6\3\6\3\6\3\6\3\6\5\6J\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7" +
          "\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7d\n\7" +
          "\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3" +
          "\b\5\bx\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u0086" +
          "\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0096" +
          "\n\n\3\13\3\13\3\13\3\13\5\13\u009c\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3" +
          "\f\3\f\3\f\3\f\3\f\5\f\u00aa\n\f\3\r\6\r\u00ad\n\r\r\r\16\r\u00ae\3\16" +
          "\3\16\7\16\u00b3\n\16\f\16\16\16\u00b6\13\16\3\16\3\16\3\16\7\16\u00bb" +
          "\n\16\f\16\16\16\u00be\13\16\3\16\5\16\u00c1\n\16\3\17\3\17\3\20\3\20" +
          "\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20" +
          "\3\20\3\20\5\20\u00d7\n\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21" +
          "\u00e1\n\21\3\22\3\22\7\22\u00e5\n\22\f\22\16\22\u00e8\13\22\3\23\3\23" +
          "\7\23\u00ec\n\23\f\23\16\23\u00ef\13\23\3\24\3\24\7\24\u00f3\n\24\f\24" +
          "\16\24\u00f6\13\24\3\24\5\24\u00f9\n\24\3\25\3\25\5\25\u00fd\n\25\3\26" +
          "\3\26\3\26\3\26\5\26\u0103\n\26\3\27\3\27\3\27\3\27\3\30\3\30\3\30\5\30" +
          "\u010c\n\30\2\2\31\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r" +
          "\31\16\33\17\35\20\37\21!\22#\23%\24\'\2)\2+\2-\25/\26\3\2\r\3\2\62;\6" +
          "\2\f\f\17\17$$^^\6\2\f\f\17\17))^^\5\2\f\f\17\17^^\4\2\62;aa\5\2C\\aa" +
          "c|\4\2\2\u0081\ud802\udc01\3\2\ud802\udc01\3\2\udc02\ue001\4\2\13\13\"" +
          "\"\4\2\f\f\17\17\2\u0121\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2" +
          "\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25" +
          "\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2" +
          "\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\3\61\3\2\2" +
          "\2\5\63\3\2\2\2\7\65\3\2\2\2\tA\3\2\2\2\13I\3\2\2\2\rc\3\2\2\2\17w\3\2" +
          "\2\2\21\u0085\3\2\2\2\23\u0095\3\2\2\2\25\u009b\3\2\2\2\27\u00a9\3\2\2" +
          "\2\31\u00ac\3\2\2\2\33\u00c0\3\2\2\2\35\u00c2\3\2\2\2\37\u00d6\3\2\2\2" +
          "!\u00e0\3\2\2\2#\u00e2\3\2\2\2%\u00e9\3\2\2\2\'\u00f0\3\2\2\2)\u00fc\3" +
          "\2\2\2+\u0102\3\2\2\2-\u0104\3\2\2\2/\u010b\3\2\2\2\61\62\7.\2\2\62\4" +
          "\3\2\2\2\63\64\7]\2\2\64\6\3\2\2\2\65\66\7_\2\2\66\b\3\2\2\2\678\7d\2" +
          "\289\7g\2\29:\7i\2\2:;\7k\2\2;B\7p\2\2<=\7D\2\2=>\7G\2\2>?\7I\2\2?@\7" +
          "K\2\2@B\7P\2\2A\67\3\2\2\2A<\3\2\2\2B\n\3\2\2\2CD\7g\2\2DE\7p\2\2EJ\7" +
          "f\2\2FG\7G\2\2GH\7P\2\2HJ\7F\2\2IC\3\2\2\2IF\3\2\2\2J\f\3\2\2\2KL\7f\2" +
          "\2LM\7g\2\2MN\7h\2\2NO\7k\2\2OP\7p\2\2PQ\7g\2\2QR\7\"\2\2RS\7o\2\2ST\7" +
          "c\2\2TU\7e\2\2UV\7t\2\2Vd\7q\2\2WX\7F\2\2XY\7G\2\2YZ\7H\2\2Z[\7K\2\2[" +
          "\\\7P\2\2\\]\7G\2\2]^\7\"\2\2^_\7O\2\2_`\7C\2\2`a\7E\2\2ab\7T\2\2bd\7" +
          "Q\2\2cK\3\2\2\2cW\3\2\2\2d\16\3\2\2\2ef\7w\2\2fg\7u\2\2gh\7g\2\2hi\7\"" +
          "\2\2ij\7o\2\2jk\7c\2\2kl\7e\2\2lm\7t\2\2mx\7q\2\2no\7W\2\2op\7U\2\2pq" +
          "\7G\2\2qr\7\"\2\2rs\7O\2\2st\7C\2\2tu\7E\2\2uv\7T\2\2vx\7Q\2\2we\3\2\2" +
          "\2wn\3\2\2\2x\20\3\2\2\2yz\7k\2\2z{\7o\2\2{|\7r\2\2|}\7q\2\2}~\7t\2\2" +
          "~\u0086\7v\2\2\177\u0080\7K\2\2\u0080\u0081\7O\2\2\u0081\u0082\7R\2\2" +
          "\u0082\u0083\7Q\2\2\u0083\u0084\7T\2\2\u0084\u0086\7V\2\2\u0085y\3\2\2" +
          "\2\u0085\177\3\2\2\2\u0086\22\3\2\2\2\u0087\u0088\7h\2\2\u0088\u0089\7" +
          "q\2\2\u0089\u008a\7t\2\2\u008a\u008b\7g\2\2\u008b\u008c\7c\2\2\u008c\u008d" +
          "\7e\2\2\u008d\u0096\7j\2\2\u008e\u008f\7H\2\2\u008f\u0090\7Q\2\2\u0090" +
          "\u0091\7T\2\2\u0091\u0092\7G\2\2\u0092\u0093\7C\2\2\u0093\u0094\7E\2\2" +
          "\u0094\u0096\7J\2\2\u0095\u0087\3\2\2\2\u0095\u008e\3\2\2\2\u0096\24\3" +
          "\2\2\2\u0097\u0098\7k\2\2\u0098\u009c\7p\2\2\u0099\u009a\7K\2\2\u009a" +
          "\u009c\7P\2\2\u009b\u0097\3\2\2\2\u009b\u0099\3\2\2\2\u009c\26\3\2\2\2" +
          "\u009d\u009e\7f\2\2\u009e\u009f\7g\2\2\u009f\u00a0\7h\2\2\u00a0\u00a1" +
          "\7k\2\2\u00a1\u00a2\7p\2\2\u00a2\u00aa\7g\2\2\u00a3\u00a4\7F\2\2\u00a4" +
          "\u00a5\7G\2\2\u00a5\u00a6\7H\2\2\u00a6\u00a7\7K\2\2\u00a7\u00a8\7P\2\2" +
          "\u00a8\u00aa\7G\2\2\u00a9\u009d\3\2\2\2\u00a9\u00a3\3\2\2\2\u00aa\30\3" +
          "\2\2\2\u00ab\u00ad\t\2\2\2\u00ac\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae" +
          "\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\32\3\2\2\2\u00b0\u00b4\7$\2\2" +
          "\u00b1\u00b3\n\3\2\2\u00b2\u00b1\3\2\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2" +
          "\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b7\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7" +
          "\u00c1\7$\2\2\u00b8\u00bc\7)\2\2\u00b9\u00bb\n\4\2\2\u00ba\u00b9\3\2\2" +
          "\2\u00bb\u00be\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00bf" +
          "\3\2\2\2\u00be\u00bc\3\2\2\2\u00bf\u00c1\7)\2\2\u00c0\u00b0\3\2\2\2\u00c0" +
          "\u00b8\3\2\2\2\u00c1\34\3\2\2\2\u00c2\u00c3\7&\2\2\u00c3\36\3\2\2\2\u00c4" +
          "\u00c5\7v\2\2\u00c5\u00c6\7t\2\2\u00c6\u00c7\7w\2\2\u00c7\u00d7\7g\2\2" +
          "\u00c8\u00c9\7V\2\2\u00c9\u00ca\7T\2\2\u00ca\u00cb\7W\2\2\u00cb\u00d7" +
          "\7G\2\2\u00cc\u00cd\7h\2\2\u00cd\u00ce\7c\2\2\u00ce\u00cf\7n\2\2\u00cf" +
          "\u00d0\7u\2\2\u00d0\u00d7\7g\2\2\u00d1\u00d2\7H\2\2\u00d2\u00d3\7C\2\2" +
          "\u00d3\u00d4\7N\2\2\u00d4\u00d5\7U\2\2\u00d5\u00d7\7G\2\2\u00d6\u00c4" +
          "\3\2\2\2\u00d6\u00c8\3\2\2\2\u00d6\u00cc\3\2\2\2\u00d6\u00d1\3\2\2\2\u00d7" +
          " \3\2\2\2\u00d8\u00d9\7p\2\2\u00d9\u00da\7w\2\2\u00da\u00db\7n\2\2\u00db" +
          "\u00e1\7n\2\2\u00dc\u00dd\7P\2\2\u00dd\u00de\7W\2\2\u00de\u00df\7N\2\2" +
          "\u00df\u00e1\7N\2\2\u00e0\u00d8\3\2\2\2\u00e0\u00dc\3\2\2\2\u00e1\"\3" +
          "\2\2\2\u00e2\u00e6\5+\26\2\u00e3\u00e5\5)\25\2\u00e4\u00e3\3\2\2\2\u00e5" +
          "\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7$\3\2\2\2" +
          "\u00e8\u00e6\3\2\2\2\u00e9\u00ed\7%\2\2\u00ea\u00ec\n\5\2\2\u00eb\u00ea" +
          "\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee" +
          "&\3\2\2\2\u00ef\u00ed\3\2\2\2\u00f0\u00f8\t\2\2\2\u00f1\u00f3\t\6\2\2" +
          "\u00f2\u00f1\3\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5" +
          "\3\2\2\2\u00f5\u00f7\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f7\u00f9\t\2\2\2\u00f8" +
          "\u00f4\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9(\3\2\2\2\u00fa\u00fd\5+\26\2" +
          "\u00fb\u00fd\t\2\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fb\3\2\2\2\u00fd*\3" +
          "\2\2\2\u00fe\u0103\t\7\2\2\u00ff\u0103\n\b\2\2\u0100\u0101\t\t\2\2\u0101" +
          "\u0103\t\n\2\2\u0102\u00fe\3\2\2\2\u0102\u00ff\3\2\2\2\u0102\u0100\3\2" +
          "\2\2\u0103,\3\2\2\2\u0104\u0105\t\13\2\2\u0105\u0106\3\2\2\2\u0106\u0107" +
          "\b\27\2\2\u0107.\3\2\2\2\u0108\u0109\7\17\2\2\u0109\u010c\7\f\2\2\u010a" +
          "\u010c\t\f\2\2\u010b\u0108\3\2\2\2\u010b\u010a\3\2\2\2\u010c\60\3\2\2" +
          "\2\30\2AIcw\u0085\u0095\u009b\u00a9\u00ae\u00b4\u00bc\u00c0\u00d6\u00e0" +
          "\u00e6\u00ed\u00f4\u00f8\u00fc\u0102\u010b\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}