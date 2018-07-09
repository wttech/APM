grammar ApmLang;

/*
 * Parser Rules
 */

apm
    : (line? EOL)+ line?
    ;

line
    : (command | macroDefinition | scriptInclusion | comment)
    ;

name
    : IDENTIFIER
    ;

array
    : ARRAY_BEGIN value (',' value)* ARRAY_END
    ;

value
    : variable
    | IDENTIFIER
    | STRING_LITERAL
    ;

variable
    : '${' IDENTIFIER '}'
    ;

parameter
    : array
    | variable
    | IDENTIFIER
    | STRING_LITERAL
    ;

comment
    : COMMENT
    ;

command
    : EXECUTE_MACRO name parametersInvokation? # MacroExecution
    | IDENTIFIER parameter+ # GenericCommand
    ;

parametersDefinition
    : '(' IDENTIFIER (',' IDENTIFIER)* ')'
    ;

parametersInvokation
    : '(' parameter (',' parameter)* ')'
    ;

body
    : (command? EOL)+
    ;

scriptInclusion
    : INCLUDE_SCRIPT parameter
    ;

macroDefinition
    : DEFINE_MACRO name parametersDefinition? EOL? BLOCK_BEGIN EOL? body BLOCK_END
    ;

/*
 * Lexer Rules
 */

//keywords
ARRAY_BEGIN
    : '['
    ;
ARRAY_END
    : ']'
    ;
BLOCK_BEGIN
    : 'begin'
    | 'BEGIN'
    ;
BLOCK_END
    : 'end'
    | 'END'
    ;
DEFINE_MACRO
    : 'define macro'
    | 'DEFINE MACRO'
    ;
EXECUTE_MACRO
    : 'use macro'
    | 'USE MACRO'
    ;
INCLUDE_SCRIPT
    : 'include'
    | 'INCLUDE'
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

