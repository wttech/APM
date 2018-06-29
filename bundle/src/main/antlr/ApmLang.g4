grammar ApmLang;

/*
 * Parser Rules
 */

apm
    : (command | macroDefinition | comment | EOL)+ EOF
    ;

name
    : IDENTIFIER
    ;

variable
    : '${' IDENTIFIER '}'
    ;

parameter
    : variable
    | IDENTIFIER
    | STRING_LITERAL
    ;

comment
    : COMMENT EOL
    ;

command
    : USE_MACRO name parametersInvokation? EOL # CommandUseMacro
    | ALLOW parameter parameter? EOL # CommandAllow
    | IDENTIFIER parameter+ EOL # CommandGeneric
    ;

parametersDefinition
    : '(' IDENTIFIER (',' IDENTIFIER)* ')'
    ;

parametersInvokation
    : '(' parameter (',' parameter)* ')'
    ;

body
    : command+
    ;

macroDefinition
    : DEFINE_MACRO name parametersDefinition? EOL? BEGIN EOL? body END EOL
    ;

/*
 * Lexer Rules
 */

//keywords
ALLOW
    : 'allow'
    | 'ALLOW'
    ;
BEGIN
    : 'begin'
    | 'BEGIN'
    ;
END
    : 'end'
    | 'END'
    ;
DEFINE_MACRO
    : 'define macro'
    | 'DEFINE MACRO'
    ;
USE_MACRO
    : 'use macro'
    | 'USE MACRO'
    ;
STRING_LITERAL
    : '"' (~["\\\r\n] )* '"'
    | '\'' (~['\\\r\n] )* '\''
    ;
IDENTIFIER
    : Letter LetterOrDigit*
    ;
COMMENT
    : '#' (~[\\\r\n] )*
    ;

fragment Digits
    : [0-9] ([0-9_]* [0-9])?
    ;
fragment LetterOrDigit
    : Letter
    | [0-9]
    ;
fragment Letter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;
WHITESPACE
    : (' ' | '\t') -> skip
    ;
EOL
    : ('\r\n' | '\r' | '\n')
    ;

