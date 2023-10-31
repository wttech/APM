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
    : command+ EOF
    ;

name
    : IDENTIFIER
    ;

privilegeName
    : IDENTIFIER COLON IDENTIFIER
    ;

path
    : STRING_LITERAL
    | PATH_IDENTIFIER
    ;

array
    : ARRAY_BEGIN (arrayValue (COMMA arrayValue)*)? ARRAY_END
    ;

arrayValue
    : value
    | name
    | privilegeName
    | argument
    ;

structure
    : STRUCTURE_BEGIN (structureEntry (COMMA structureEntry)*)? STRUCTURE_END
    ;

structureEntry
    : structureKey COLON structureValue
    ;

structureKey
    : IDENTIFIER
    | STRING_LITERAL
    ;

structureValue
    : value
    | argument
    ;

variable
    : VARIABLE_PREFIX variableIdentifier
    | VARIABLE_PREFIX STRUCTURE_BEGIN variableIdentifier STRUCTURE_END
    ;

variableIdentifier
    : IDENTIFIER
    | VARIABLE_IDENTIFIER
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
    | array
    | structure
    ;

plus
    : '+'
    ;

expression
    : expression plus expression
    | value
    ;

argument
    : expression
    | path
    ;

command
    : RUN_SCRIPT path namedArguments? # RunScript
    | IMPORT_SCRIPT path (AS name)? # ImportScript
    | FOR_EACH IDENTIFIER IN argument body # ForEach
    | DEFINE IDENTIFIER argument # DefineVariable
    | REQUIRE IDENTIFIER # RequireVariable
    | (ALLOW | DENY) argument ON? complexArguments # AllowDenyCommand
    | commandName complexArguments? body? # GenericCommand
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
    : BLOCK_BEGIN command* BLOCK_END
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
STRUCTURE_BEGIN
    : '{'
    ;
STRUCTURE_END
    : '}'
    ;
COMMA
    : ','
    ;
COLON
    : ':'
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
ON
    : 'ON'
    | 'on'
    ;
ALLOW
    : 'ALLOW'
    | 'allow'
    ;
DENY
    : 'DENY'
    | 'deny'
    ;
NUMBER_LITERAL
    : [0-9]+
    ;
STRING_LITERAL
    : '"' (~["\r\n] )* '"'
    | '\'' (~['\r\n] )* '\''
    ;
VARIABLE_PREFIX
    : '$'
    ;
IDENTIFIER
    : Letter LetterOrDigit*
    ;
EXTENDED_IDENTIFIER
    : IdentifierPart ('-' IdentifierPart)*
    ;
PATH_IDENTIFIER
    : PathPart+
    ;
VARIABLE_IDENTIFIER
    : VariablePart ('.' VariablePart)*
    ;
COMMENT
    : '#' (~[\r\n] )* -> skip
    ;
fragment LetterOrDigit
    : Letter
    | Digit
    ;
fragment Letter
    : [a-zA-Z_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;
fragment Digit
    : [0-9]
    ;
fragment IdentifierPart
    : Letter LetterOrDigit*
    ;
fragment VariablePart
    : IdentifierPart (ARRAY_BEGIN LetterOrDigit+ ARRAY_END)?
    ;
fragment PathPart
    : '/' (~[\r\n\t ])+
    ;
WHITESPACE
    : (' ' | '\t') -> skip
    ;
EOL
    : ('\r' | '\n') -> skip
    ;
