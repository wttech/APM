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
			T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, ARRAY_BEGIN = 6, ARRAY_END = 7, BLOCK_BEGIN = 8,
			BLOCK_END = 9, DEFINE_MACRO = 10, EXECUTE_MACRO = 11, INCLUDE_SCRIPT = 12, STRING_LITERAL = 13,
			IDENTIFIER = 14, COMMENT = 15, WHITESPACE = 16, EOL = 17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
			"T__0", "T__1", "T__2", "T__3", "T__4", "ARRAY_BEGIN", "ARRAY_END", "BLOCK_BEGIN",
			"BLOCK_END", "DEFINE_MACRO", "EXECUTE_MACRO", "INCLUDE_SCRIPT", "STRING_LITERAL",
			"IDENTIFIER", "COMMENT", "Digits", "LetterOrDigit", "Letter", "WHITESPACE",
			"EOL"
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
			"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23\u00c9\b\1\4\2" +
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
					"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\5\3" +
					"\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t" +
					"E\n\t\3\n\3\n\3\n\3\n\3\n\3\n\5\nM\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3" +
					"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3" +
					"\13\3\13\3\13\3\13\5\13g\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f" +
					"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f{\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r" +
					"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u008b\n\r\3\16\3\16\7\16\u008f\n\16\f" +
					"\16\16\16\u0092\13\16\3\16\3\16\3\16\7\16\u0097\n\16\f\16\16\16\u009a" +
					"\13\16\3\16\5\16\u009d\n\16\3\17\3\17\7\17\u00a1\n\17\f\17\16\17\u00a4" +
					"\13\17\3\20\3\20\7\20\u00a8\n\20\f\20\16\20\u00ab\13\20\3\21\3\21\7\21" +
					"\u00af\n\21\f\21\16\21\u00b2\13\21\3\21\5\21\u00b5\n\21\3\22\3\22\5\22" +
					"\u00b9\n\22\3\23\3\23\3\23\3\23\5\23\u00bf\n\23\3\24\3\24\3\24\3\24\3" +
					"\25\3\25\3\25\5\25\u00c8\n\25\2\2\26\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21" +
					"\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\2#\2%\2\'\22)\23\3\2\r\6\2" +
					"\f\f\17\17$$^^\6\2\f\f\17\17))^^\5\2\f\f\17\17^^\3\2\62;\4\2\62;aa\6\2" +
					"&&C\\aac|\4\2\2\u0081\ud802\udc01\3\2\ud802\udc01\3\2\udc02\ue001\4\2" +
					"\13\13\"\"\4\2\f\f\17\17\2\u00d5\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2" +
					"\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2" +
					"\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2" +
					"\2\37\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\3+\3\2\2\2\5-\3\2\2\2\7\60\3\2\2" +
					"\2\t\62\3\2\2\2\13\64\3\2\2\2\r\66\3\2\2\2\178\3\2\2\2\21D\3\2\2\2\23" +
					"L\3\2\2\2\25f\3\2\2\2\27z\3\2\2\2\31\u008a\3\2\2\2\33\u009c\3\2\2\2\35" +
					"\u009e\3\2\2\2\37\u00a5\3\2\2\2!\u00ac\3\2\2\2#\u00b8\3\2\2\2%\u00be\3" +
					"\2\2\2\'\u00c0\3\2\2\2)\u00c7\3\2\2\2+,\7.\2\2,\4\3\2\2\2-.\7&\2\2./\7" +
					"}\2\2/\6\3\2\2\2\60\61\7\177\2\2\61\b\3\2\2\2\62\63\7*\2\2\63\n\3\2\2" +
					"\2\64\65\7+\2\2\65\f\3\2\2\2\66\67\7]\2\2\67\16\3\2\2\289\7_\2\29\20\3" +
					"\2\2\2:;\7d\2\2;<\7g\2\2<=\7i\2\2=>\7k\2\2>E\7p\2\2?@\7D\2\2@A\7G\2\2" +
					"AB\7I\2\2BC\7K\2\2CE\7P\2\2D:\3\2\2\2D?\3\2\2\2E\22\3\2\2\2FG\7g\2\2G" +
					"H\7p\2\2HM\7f\2\2IJ\7G\2\2JK\7P\2\2KM\7F\2\2LF\3\2\2\2LI\3\2\2\2M\24\3" +
					"\2\2\2NO\7f\2\2OP\7g\2\2PQ\7h\2\2QR\7k\2\2RS\7p\2\2ST\7g\2\2TU\7\"\2\2" +
					"UV\7o\2\2VW\7c\2\2WX\7e\2\2XY\7t\2\2Yg\7q\2\2Z[\7F\2\2[\\\7G\2\2\\]\7" +
					"H\2\2]^\7K\2\2^_\7P\2\2_`\7G\2\2`a\7\"\2\2ab\7O\2\2bc\7C\2\2cd\7E\2\2" +
					"de\7T\2\2eg\7Q\2\2fN\3\2\2\2fZ\3\2\2\2g\26\3\2\2\2hi\7w\2\2ij\7u\2\2j" +
					"k\7g\2\2kl\7\"\2\2lm\7o\2\2mn\7c\2\2no\7e\2\2op\7t\2\2p{\7q\2\2qr\7W\2" +
					"\2rs\7U\2\2st\7G\2\2tu\7\"\2\2uv\7O\2\2vw\7C\2\2wx\7E\2\2xy\7T\2\2y{\7" +
					"Q\2\2zh\3\2\2\2zq\3\2\2\2{\30\3\2\2\2|}\7k\2\2}~\7p\2\2~\177\7e\2\2\177" +
					"\u0080\7n\2\2\u0080\u0081\7w\2\2\u0081\u0082\7f\2\2\u0082\u008b\7g\2\2" +
					"\u0083\u0084\7K\2\2\u0084\u0085\7P\2\2\u0085\u0086\7E\2\2\u0086\u0087" +
					"\7N\2\2\u0087\u0088\7W\2\2\u0088\u0089\7F\2\2\u0089\u008b\7G\2\2\u008a" +
					"|\3\2\2\2\u008a\u0083\3\2\2\2\u008b\32\3\2\2\2\u008c\u0090\7$\2\2\u008d" +
					"\u008f\n\2\2\2\u008e\u008d\3\2\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2" +
					"\2\2\u0090\u0091\3\2\2\2\u0091\u0093\3\2\2\2\u0092\u0090\3\2\2\2\u0093" +
					"\u009d\7$\2\2\u0094\u0098\7)\2\2\u0095\u0097\n\3\2\2\u0096\u0095\3\2\2" +
					"\2\u0097\u009a\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009b" +
					"\3\2\2\2\u009a\u0098\3\2\2\2\u009b\u009d\7)\2\2\u009c\u008c\3\2\2\2\u009c" +
					"\u0094\3\2\2\2\u009d\34\3\2\2\2\u009e\u00a2\5%\23\2\u009f\u00a1\5#\22" +
					"\2\u00a0\u009f\3\2\2\2\u00a1\u00a4\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3" +
					"\3\2\2\2\u00a3\36\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a5\u00a9\7%\2\2\u00a6" +
					"\u00a8\n\4\2\2\u00a7\u00a6\3\2\2\2\u00a8\u00ab\3\2\2\2\u00a9\u00a7\3\2" +
					"\2\2\u00a9\u00aa\3\2\2\2\u00aa \3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\u00b4" +
					"\t\5\2\2\u00ad\u00af\t\6\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b2\3\2\2\2\u00b0" +
					"\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b3\3\2\2\2\u00b2\u00b0\3\2" +
					"\2\2\u00b3\u00b5\t\5\2\2\u00b4\u00b0\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5" +
					"\"\3\2\2\2\u00b6\u00b9\5%\23\2\u00b7\u00b9\t\5\2\2\u00b8\u00b6\3\2\2\2" +
					"\u00b8\u00b7\3\2\2\2\u00b9$\3\2\2\2\u00ba\u00bf\t\7\2\2\u00bb\u00bf\n" +
					"\b\2\2\u00bc\u00bd\t\t\2\2\u00bd\u00bf\t\n\2\2\u00be\u00ba\3\2\2\2\u00be" +
					"\u00bb\3\2\2\2\u00be\u00bc\3\2\2\2\u00bf&\3\2\2\2\u00c0\u00c1\t\13\2\2" +
					"\u00c1\u00c2\3\2\2\2\u00c2\u00c3\b\24\2\2\u00c3(\3\2\2\2\u00c4\u00c5\7" +
					"\17\2\2\u00c5\u00c8\7\f\2\2\u00c6\u00c8\t\f\2\2\u00c7\u00c4\3\2\2\2\u00c7" +
					"\u00c6\3\2\2\2\u00c8*\3\2\2\2\22\2DLfz\u008a\u0090\u0098\u009c\u00a2\u00a9" +
					"\u00b0\u00b4\u00b8\u00be\u00c7\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}