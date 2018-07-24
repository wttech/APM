// Generated from ApmLang.g4 by ANTLR 4.7.1
package com.cognifide.apm.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ApmLangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, ARRAY_BEGIN=2, ARRAY_END=3, BLOCK_BEGIN=4, BLOCK_END=5, DEFINE_MACRO=6, 
		EXECUTE_MACRO=7, IMPORT_SCRIPT=8, DEFINE=9, NUMBER_LITERAL=10, STRING_LITERAL=11, 
		VARIABLE_PREFIX=12, BOOLEAN_VALUE=13, NULL_VALUE=14, IDENTIFIER=15, COMMENT=16, 
		WHITESPACE=17, EOL=18;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "ARRAY_BEGIN", "ARRAY_END", "BLOCK_BEGIN", "BLOCK_END", "DEFINE_MACRO", 
		"EXECUTE_MACRO", "IMPORT_SCRIPT", "DEFINE", "NUMBER_LITERAL", "STRING_LITERAL", 
		"VARIABLE_PREFIX", "BOOLEAN_VALUE", "NULL_VALUE", "IDENTIFIER", "COMMENT", 
		"Digits", "LetterOrDigit", "Letter", "WHITESPACE", "EOL"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "'['", "']'", null, null, null, null, null, null, null, null, 
		"'$'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "ARRAY_BEGIN", "ARRAY_END", "BLOCK_BEGIN", "BLOCK_END", "DEFINE_MACRO", 
		"EXECUTE_MACRO", "IMPORT_SCRIPT", "DEFINE", "NUMBER_LITERAL", "STRING_LITERAL", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\24\u00f3\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5>\n\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\5\6F\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7`\n\7\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bt\n\b\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u0082\n\t\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0090\n\n\3\13\6\13\u0093\n\13\r"+
		"\13\16\13\u0094\3\f\3\f\7\f\u0099\n\f\f\f\16\f\u009c\13\f\3\f\3\f\3\f"+
		"\7\f\u00a1\n\f\f\f\16\f\u00a4\13\f\3\f\5\f\u00a7\n\f\3\r\3\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\5\16\u00bd\n\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17"+
		"\u00c7\n\17\3\20\3\20\7\20\u00cb\n\20\f\20\16\20\u00ce\13\20\3\21\3\21"+
		"\7\21\u00d2\n\21\f\21\16\21\u00d5\13\21\3\22\3\22\7\22\u00d9\n\22\f\22"+
		"\16\22\u00dc\13\22\3\22\5\22\u00df\n\22\3\23\3\23\5\23\u00e3\n\23\3\24"+
		"\3\24\3\24\3\24\5\24\u00e9\n\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\5\26"+
		"\u00f2\n\26\2\2\27\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r"+
		"\31\16\33\17\35\20\37\21!\22#\2%\2\'\2)\23+\24\3\2\r\3\2\62;\6\2\f\f\17"+
		"\17$$^^\6\2\f\f\17\17))^^\5\2\f\f\17\17^^\4\2\62;aa\5\2C\\aac|\4\2\2\u0081"+
		"\ud802\udc01\3\2\ud802\udc01\3\2\udc02\ue001\4\2\13\13\"\"\4\2\f\f\17"+
		"\17\2\u0105\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2)\3\2\2\2\2+\3\2\2\2\3-\3\2\2\2\5/\3\2\2\2\7\61\3\2\2\2\t=\3\2\2\2"+
		"\13E\3\2\2\2\r_\3\2\2\2\17s\3\2\2\2\21\u0081\3\2\2\2\23\u008f\3\2\2\2"+
		"\25\u0092\3\2\2\2\27\u00a6\3\2\2\2\31\u00a8\3\2\2\2\33\u00bc\3\2\2\2\35"+
		"\u00c6\3\2\2\2\37\u00c8\3\2\2\2!\u00cf\3\2\2\2#\u00d6\3\2\2\2%\u00e2\3"+
		"\2\2\2\'\u00e8\3\2\2\2)\u00ea\3\2\2\2+\u00f1\3\2\2\2-.\7.\2\2.\4\3\2\2"+
		"\2/\60\7]\2\2\60\6\3\2\2\2\61\62\7_\2\2\62\b\3\2\2\2\63\64\7d\2\2\64\65"+
		"\7g\2\2\65\66\7i\2\2\66\67\7k\2\2\67>\7p\2\289\7D\2\29:\7G\2\2:;\7I\2"+
		"\2;<\7K\2\2<>\7P\2\2=\63\3\2\2\2=8\3\2\2\2>\n\3\2\2\2?@\7g\2\2@A\7p\2"+
		"\2AF\7f\2\2BC\7G\2\2CD\7P\2\2DF\7F\2\2E?\3\2\2\2EB\3\2\2\2F\f\3\2\2\2"+
		"GH\7f\2\2HI\7g\2\2IJ\7h\2\2JK\7k\2\2KL\7p\2\2LM\7g\2\2MN\7\"\2\2NO\7o"+
		"\2\2OP\7c\2\2PQ\7e\2\2QR\7t\2\2R`\7q\2\2ST\7F\2\2TU\7G\2\2UV\7H\2\2VW"+
		"\7K\2\2WX\7P\2\2XY\7G\2\2YZ\7\"\2\2Z[\7O\2\2[\\\7C\2\2\\]\7E\2\2]^\7T"+
		"\2\2^`\7Q\2\2_G\3\2\2\2_S\3\2\2\2`\16\3\2\2\2ab\7w\2\2bc\7u\2\2cd\7g\2"+
		"\2de\7\"\2\2ef\7o\2\2fg\7c\2\2gh\7e\2\2hi\7t\2\2it\7q\2\2jk\7W\2\2kl\7"+
		"U\2\2lm\7G\2\2mn\7\"\2\2no\7O\2\2op\7C\2\2pq\7E\2\2qr\7T\2\2rt\7Q\2\2"+
		"sa\3\2\2\2sj\3\2\2\2t\20\3\2\2\2uv\7k\2\2vw\7o\2\2wx\7r\2\2xy\7q\2\2y"+
		"z\7t\2\2z\u0082\7v\2\2{|\7K\2\2|}\7O\2\2}~\7R\2\2~\177\7Q\2\2\177\u0080"+
		"\7T\2\2\u0080\u0082\7V\2\2\u0081u\3\2\2\2\u0081{\3\2\2\2\u0082\22\3\2"+
		"\2\2\u0083\u0084\7f\2\2\u0084\u0085\7g\2\2\u0085\u0086\7h\2\2\u0086\u0087"+
		"\7k\2\2\u0087\u0088\7p\2\2\u0088\u0090\7g\2\2\u0089\u008a\7F\2\2\u008a"+
		"\u008b\7G\2\2\u008b\u008c\7H\2\2\u008c\u008d\7K\2\2\u008d\u008e\7P\2\2"+
		"\u008e\u0090\7G\2\2\u008f\u0083\3\2\2\2\u008f\u0089\3\2\2\2\u0090\24\3"+
		"\2\2\2\u0091\u0093\t\2\2\2\u0092\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094"+
		"\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\26\3\2\2\2\u0096\u009a\7$\2\2"+
		"\u0097\u0099\n\3\2\2\u0098\u0097\3\2\2\2\u0099\u009c\3\2\2\2\u009a\u0098"+
		"\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009d\3\2\2\2\u009c\u009a\3\2\2\2\u009d"+
		"\u00a7\7$\2\2\u009e\u00a2\7)\2\2\u009f\u00a1\n\4\2\2\u00a0\u009f\3\2\2"+
		"\2\u00a1\u00a4\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a5"+
		"\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a5\u00a7\7)\2\2\u00a6\u0096\3\2\2\2\u00a6"+
		"\u009e\3\2\2\2\u00a7\30\3\2\2\2\u00a8\u00a9\7&\2\2\u00a9\32\3\2\2\2\u00aa"+
		"\u00ab\7v\2\2\u00ab\u00ac\7t\2\2\u00ac\u00ad\7w\2\2\u00ad\u00bd\7g\2\2"+
		"\u00ae\u00af\7V\2\2\u00af\u00b0\7T\2\2\u00b0\u00b1\7W\2\2\u00b1\u00bd"+
		"\7G\2\2\u00b2\u00b3\7h\2\2\u00b3\u00b4\7c\2\2\u00b4\u00b5\7n\2\2\u00b5"+
		"\u00b6\7u\2\2\u00b6\u00bd\7g\2\2\u00b7\u00b8\7H\2\2\u00b8\u00b9\7C\2\2"+
		"\u00b9\u00ba\7N\2\2\u00ba\u00bb\7U\2\2\u00bb\u00bd\7G\2\2\u00bc\u00aa"+
		"\3\2\2\2\u00bc\u00ae\3\2\2\2\u00bc\u00b2\3\2\2\2\u00bc\u00b7\3\2\2\2\u00bd"+
		"\34\3\2\2\2\u00be\u00bf\7p\2\2\u00bf\u00c0\7w\2\2\u00c0\u00c1\7n\2\2\u00c1"+
		"\u00c7\7n\2\2\u00c2\u00c3\7P\2\2\u00c3\u00c4\7W\2\2\u00c4\u00c5\7N\2\2"+
		"\u00c5\u00c7\7N\2\2\u00c6\u00be\3\2\2\2\u00c6\u00c2\3\2\2\2\u00c7\36\3"+
		"\2\2\2\u00c8\u00cc\5\'\24\2\u00c9\u00cb\5%\23\2\u00ca\u00c9\3\2\2\2\u00cb"+
		"\u00ce\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd \3\2\2\2"+
		"\u00ce\u00cc\3\2\2\2\u00cf\u00d3\7%\2\2\u00d0\u00d2\n\5\2\2\u00d1\u00d0"+
		"\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4"+
		"\"\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d6\u00de\t\2\2\2\u00d7\u00d9\t\6\2\2"+
		"\u00d8\u00d7\3\2\2\2\u00d9\u00dc\3\2\2\2\u00da\u00d8\3\2\2\2\u00da\u00db"+
		"\3\2\2\2\u00db\u00dd\3\2\2\2\u00dc\u00da\3\2\2\2\u00dd\u00df\t\2\2\2\u00de"+
		"\u00da\3\2\2\2\u00de\u00df\3\2\2\2\u00df$\3\2\2\2\u00e0\u00e3\5\'\24\2"+
		"\u00e1\u00e3\t\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e1\3\2\2\2\u00e3&\3"+
		"\2\2\2\u00e4\u00e9\t\7\2\2\u00e5\u00e9\n\b\2\2\u00e6\u00e7\t\t\2\2\u00e7"+
		"\u00e9\t\n\2\2\u00e8\u00e4\3\2\2\2\u00e8\u00e5\3\2\2\2\u00e8\u00e6\3\2"+
		"\2\2\u00e9(\3\2\2\2\u00ea\u00eb\t\13\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ed"+
		"\b\25\2\2\u00ed*\3\2\2\2\u00ee\u00ef\7\17\2\2\u00ef\u00f2\7\f\2\2\u00f0"+
		"\u00f2\t\f\2\2\u00f1\u00ee\3\2\2\2\u00f1\u00f0\3\2\2\2\u00f2,\3\2\2\2"+
		"\26\2=E_s\u0081\u008f\u0094\u009a\u00a2\u00a6\u00bc\u00c6\u00cc\u00d3"+
		"\u00da\u00de\u00e2\u00e8\u00f1\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}