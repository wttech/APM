/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

grammar ApmLang;

/*
 * Parser Rules
 */

apm
    : (line? EOL)+ line?
    ;

line
    : command
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
    : RUN_SCRIPT path namedArguments? # RunScript
    | IMPORT_SCRIPT path (AS name)? # ImportScript
    | FOR_EACH IDENTIFIER IN argument EOL? body # ForEach
    | DEFINE IDENTIFIER argument # DefineVariable
    | REQUIRE IDENTIFIER # RequireVariable
    | commandName complexArguments? EOL? body? # GenericCommand
    ;

commandName
    : identifier
    ;

identifier
    : IDENTIFIER
    | EXTENDED_IDENTIFIER
    ;

complexArguments
    : complexArgument+
    ;

complexArgument
    : requiredArgument
    | namedArgument
    | flag
    ;

requiredArgument
    : argument
    ;

namedArguments
    : namedArgument+
    ;

namedArgument
    : IDENTIFIER '=' argument
    ;

flag
    : '--' identifier
    ;

body
    : BLOCK_BEGIN EOL? (command? EOL)+ BLOCK_END
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
IMPORT_SCRIPT
    : 'import'
    | 'IMPORT'
    ;
RUN_SCRIPT
    : 'run'
    | 'RUN'
    ;
FOR_EACH
    : 'for-each'
    | 'FOR-EACH'
    ;
IN
    : 'in'
    | 'IN'
    ;
DEFINE
    : 'define'
    | 'DEFINE'
    ;
REQUIRE
    : 'require'
    | 'REQUIRE'
    ;
AS
    : 'AS'
    | 'as'
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
EXTENDED_IDENTIFIER
    : Letter LetterOrDigitOrDash*
    ;
COMMENT
    : '#' (~[\\\r\n] )* -> skip
    ;

fragment Digits
    : [0-9] ([0-9_]* [0-9])?
    ;
fragment LetterOrDigitOrDash
    : Letter
    | '-'
    | [0-9]
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

