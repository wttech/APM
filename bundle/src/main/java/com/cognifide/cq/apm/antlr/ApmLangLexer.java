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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, ALLOW=6, BEGIN=7, END=8, DEFINE_MACRO=9, 
		USE_MACRO=10, STRING_LITERAL=11, IDENTIFIER=12, COMMENT=13, WHITESPACE=14, 
		EOL=15;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "ALLOW", "BEGIN", "END", "DEFINE_MACRO", 
		"USE_MACRO", "STRING_LITERAL", "IDENTIFIER", "COMMENT", "Digits", "LetterOrDigit", 
		"Letter", "WHITESPACE", "EOL"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\21\u00bd\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7=\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\5\bI\n\b\3\t\3\t\3\t\3\t\3\t\3\t\5\tQ\n\t\3\n\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\5\nk\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\177\n\13\3\f\3\f\7\f\u0083"+
		"\n\f\f\f\16\f\u0086\13\f\3\f\3\f\3\f\7\f\u008b\n\f\f\f\16\f\u008e\13\f"+
		"\3\f\5\f\u0091\n\f\3\r\3\r\7\r\u0095\n\r\f\r\16\r\u0098\13\r\3\16\3\16"+
		"\7\16\u009c\n\16\f\16\16\16\u009f\13\16\3\17\3\17\7\17\u00a3\n\17\f\17"+
		"\16\17\u00a6\13\17\3\17\5\17\u00a9\n\17\3\20\3\20\5\20\u00ad\n\20\3\21"+
		"\3\21\3\21\3\21\5\21\u00b3\n\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\5\23"+
		"\u00bc\n\23\2\2\24\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r"+
		"\31\16\33\17\35\2\37\2!\2#\20%\21\3\2\r\6\2\f\f\17\17$$^^\6\2\f\f\17\17"+
		"))^^\5\2\f\f\17\17^^\3\2\62;\4\2\62;aa\6\2&&C\\aac|\4\2\2\u0081\ud802"+
		"\udc01\3\2\ud802\udc01\3\2\udc02\ue001\4\2\13\13\"\"\4\2\f\f\17\17\2\u00c9"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2\2\2\5*\3\2\2"+
		"\2\7,\3\2\2\2\t.\3\2\2\2\13\60\3\2\2\2\r<\3\2\2\2\17H\3\2\2\2\21P\3\2"+
		"\2\2\23j\3\2\2\2\25~\3\2\2\2\27\u0090\3\2\2\2\31\u0092\3\2\2\2\33\u0099"+
		"\3\2\2\2\35\u00a0\3\2\2\2\37\u00ac\3\2\2\2!\u00b2\3\2\2\2#\u00b4\3\2\2"+
		"\2%\u00bb\3\2\2\2\'(\7&\2\2()\7}\2\2)\4\3\2\2\2*+\7\177\2\2+\6\3\2\2\2"+
		",-\7*\2\2-\b\3\2\2\2./\7.\2\2/\n\3\2\2\2\60\61\7+\2\2\61\f\3\2\2\2\62"+
		"\63\7c\2\2\63\64\7n\2\2\64\65\7n\2\2\65\66\7q\2\2\66=\7y\2\2\678\7C\2"+
		"\289\7N\2\29:\7N\2\2:;\7Q\2\2;=\7Y\2\2<\62\3\2\2\2<\67\3\2\2\2=\16\3\2"+
		"\2\2>?\7d\2\2?@\7g\2\2@A\7i\2\2AB\7k\2\2BI\7p\2\2CD\7D\2\2DE\7G\2\2EF"+
		"\7I\2\2FG\7K\2\2GI\7P\2\2H>\3\2\2\2HC\3\2\2\2I\20\3\2\2\2JK\7g\2\2KL\7"+
		"p\2\2LQ\7f\2\2MN\7G\2\2NO\7P\2\2OQ\7F\2\2PJ\3\2\2\2PM\3\2\2\2Q\22\3\2"+
		"\2\2RS\7f\2\2ST\7g\2\2TU\7h\2\2UV\7k\2\2VW\7p\2\2WX\7g\2\2XY\7\"\2\2Y"+
		"Z\7o\2\2Z[\7c\2\2[\\\7e\2\2\\]\7t\2\2]k\7q\2\2^_\7F\2\2_`\7G\2\2`a\7H"+
		"\2\2ab\7K\2\2bc\7P\2\2cd\7G\2\2de\7\"\2\2ef\7O\2\2fg\7C\2\2gh\7E\2\2h"+
		"i\7T\2\2ik\7Q\2\2jR\3\2\2\2j^\3\2\2\2k\24\3\2\2\2lm\7w\2\2mn\7u\2\2no"+
		"\7g\2\2op\7\"\2\2pq\7o\2\2qr\7c\2\2rs\7e\2\2st\7t\2\2t\177\7q\2\2uv\7"+
		"W\2\2vw\7U\2\2wx\7G\2\2xy\7\"\2\2yz\7O\2\2z{\7C\2\2{|\7E\2\2|}\7T\2\2"+
		"}\177\7Q\2\2~l\3\2\2\2~u\3\2\2\2\177\26\3\2\2\2\u0080\u0084\7$\2\2\u0081"+
		"\u0083\n\2\2\2\u0082\u0081\3\2\2\2\u0083\u0086\3\2\2\2\u0084\u0082\3\2"+
		"\2\2\u0084\u0085\3\2\2\2\u0085\u0087\3\2\2\2\u0086\u0084\3\2\2\2\u0087"+
		"\u0091\7$\2\2\u0088\u008c\7)\2\2\u0089\u008b\n\3\2\2\u008a\u0089\3\2\2"+
		"\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f"+
		"\3\2\2\2\u008e\u008c\3\2\2\2\u008f\u0091\7)\2\2\u0090\u0080\3\2\2\2\u0090"+
		"\u0088\3\2\2\2\u0091\30\3\2\2\2\u0092\u0096\5!\21\2\u0093\u0095\5\37\20"+
		"\2\u0094\u0093\3\2\2\2\u0095\u0098\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097"+
		"\3\2\2\2\u0097\32\3\2\2\2\u0098\u0096\3\2\2\2\u0099\u009d\7%\2\2\u009a"+
		"\u009c\n\4\2\2\u009b\u009a\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2"+
		"\2\2\u009d\u009e\3\2\2\2\u009e\34\3\2\2\2\u009f\u009d\3\2\2\2\u00a0\u00a8"+
		"\t\5\2\2\u00a1\u00a3\t\6\2\2\u00a2\u00a1\3\2\2\2\u00a3\u00a6\3\2\2\2\u00a4"+
		"\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a7\3\2\2\2\u00a6\u00a4\3\2"+
		"\2\2\u00a7\u00a9\t\5\2\2\u00a8\u00a4\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9"+
		"\36\3\2\2\2\u00aa\u00ad\5!\21\2\u00ab\u00ad\t\5\2\2\u00ac\u00aa\3\2\2"+
		"\2\u00ac\u00ab\3\2\2\2\u00ad \3\2\2\2\u00ae\u00b3\t\7\2\2\u00af\u00b3"+
		"\n\b\2\2\u00b0\u00b1\t\t\2\2\u00b1\u00b3\t\n\2\2\u00b2\u00ae\3\2\2\2\u00b2"+
		"\u00af\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b3\"\3\2\2\2\u00b4\u00b5\t\13\2"+
		"\2\u00b5\u00b6\3\2\2\2\u00b6\u00b7\b\22\2\2\u00b7$\3\2\2\2\u00b8\u00b9"+
		"\7\17\2\2\u00b9\u00bc\7\f\2\2\u00ba\u00bc\t\f\2\2\u00bb\u00b8\3\2\2\2"+
		"\u00bb\u00ba\3\2\2\2\u00bc&\3\2\2\2\22\2<HPj~\u0084\u008c\u0090\u0096"+
		"\u009d\u00a4\u00a8\u00ac\u00b2\u00bb\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}