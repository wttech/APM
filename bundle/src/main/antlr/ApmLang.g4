grammar ApmLang;

/*
 * Parser Rules
 */

apm
    : (line? EOL)+ line?
    ;

line
    : (command | macroDefinition | scriptInclusion)
    ;

name
    : IDENTIFIER
    ;

array
    : ARRAY_BEGIN value (',' value)* ARRAY_END
    ;

variable
    : VARIABLE_PREFIX IDENTIFIER
    ;

booleanValue
    : BOOLEAN_VALUE
    ;

numberValue
    : NUMBER_LITERAL
    ;

stringValue
    : STRING_LITERAL
    ;

stringConst
    : IDENTIFIER
    ;

value
    : variable
    | booleanValue
    | numberValue
    | stringValue
    | stringConst
    ;

operator
    : '+'
    ;

expression
    : expression operator expression
    | value
    ;

parameter
    : array
    | expression
    ;

command
    : EXECUTE_MACRO name parametersInvocation? # MacroExecution
    | FOR_EACH IDENTIFIER IN parameter EOL? BLOCK_BEGIN EOL? body BLOCK_END # ForEach
    | DEFINE IDENTIFIER parameter # VariableDefinition
    | IDENTIFIER parametersInvocation? # GenericCommand
    ;

parametersDefinition
    : IDENTIFIER+
    ;

parametersInvocation
    : parameter+
    ;

body
    : (command? EOL)+
    ;

path
    : STRING_LITERAL
    ;

scriptInclusion
    : IMPORT_SCRIPT INPLACE? path
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
IMPORT_SCRIPT
    : 'import'
    | 'IMPORT'
    ;
INPLACE
    : 'inplace'
    | 'INPLACE'
    ;
FOR_EACH
    : 'foreach'
    | 'FOREACH'
    ;
IN
    : 'in'
    | 'IN'
    ;
DEFINE
    : 'define'
    | 'DEFINE'
    ;
NUMBER_LITERAL
    : [0-9]+
    ;
STRING_LITERAL
    : '"' (~["\\\r\n] )* '"'
    | '\'' (~['\\\r\n] )* '\''
    ;
VARIABLE_PREFIX
    : '$'
    ;
BOOLEAN_VALUE
    : 'true'
    | 'TRUE'
    | 'false'
    | 'FALSE'
    ;
IDENTIFIER
    : Letter LetterOrDigit*
    ;
COMMENT
    : '#' (~[\\\r\n] )* -> skip
    ;

fragment Digits
    : [0-9] ([0-9_]* [0-9])?
    ;
fragment LetterOrDigit
    : Letter
    | [0-9]
    ;
fragment Letter
    : [a-zA-Z_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;
WHITESPACE
    : (' ' | '\t') -> skip
    ;
EOL
    : ('\r\n' | '\r' | '\n')
    ;

