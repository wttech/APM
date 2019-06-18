grammar ApmLang;

/*
 * Parser Rules
 */

apm
    : (line? EOL)+ line?
    ;

line
    : (command | defineMacro | importScript)
    ;

name
    : IDENTIFIER
    ;

path
    : STRING_LITERAL
    ;

array
    : ARRAY_BEGIN EOL? value (',' EOL? value)* EOL? ARRAY_END
    ;

variable
    : VARIABLE_PREFIX IDENTIFIER
    ;

numberValue
    : NUMBER_LITERAL
    ;

stringValue
    : STRING_LITERAL
    | IDENTIFIER
    ;

value
    : variable
    | numberValue
    | stringValue
    ;

plus
    : '+'
    ;

expression
    : expression plus expression
    | array
    | value
    ;

argument
    : expression
    ;

command
    : RUN_MACRO name arguments? # RunMacro
    | RUN_SCRIPT path # RunScript
    | FOR_EACH IDENTIFIER IN argument EOL? body # ForEach
    | DEFINE IDENTIFIER argument # Define
    | IDENTIFIER arguments? # GenericCommand
    ;

parameters
    : IDENTIFIER+
    ;

arguments
    : argument+
    ;

body
    : BLOCK_BEGIN EOL? (command? EOL)+ BLOCK_END
    ;

defineMacro
    : DEFINE_MACRO name parameters? EOL? body
    ;

importScript
    : IMPORT_SCRIPT path
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
RUN_MACRO
    : 'use macro'
    | 'USE MACRO'
    ;
IMPORT_SCRIPT
    : 'import'
    | 'IMPORT'
    ;
RUN_SCRIPT
    : 'run'
    | 'RUN'
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

