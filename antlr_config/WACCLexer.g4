lexer grammar WACCLexer;

// Comments

COMMENT: '#' (~'\n')* -> skip;

// WHITESPACE

WS: (' ' | '\n' | '\r' | '\t' | '\f')+ -> skip;

// Binary Operators

MULT: '*';
DIV: '/';
MOD: '%';
PLUS: '+';
MINUS: '-';
AND: '&&';
OR: '||';

// Unary Operators

NOT: '!';
LEN: 'len';
ORD: 'ord';
CHR: 'chr';
REF: '&';

// Comparators

GT: '>';
GTE: '>=';
LT: '<';
LTE: '<=';
EQ: '==';
NEQ: '!=';

// Brackets

L_PARENTHESIS: '(';
R_PARENTHESIS: ')';
L_BRACKET: '[';
R_BRACKET: ']';

// Numbers

fragment DIGIT: '0'..'9';
INTEGER: DIGIT+;

// Boolean

TRUE: 'true';
FALSE: 'false';

// Characters

fragment CHAR: ~('\\' | '\'' | '"');
fragment ESC_CHAR: '0' | 'b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\';
fragment CHARACTER: CHAR | '\\' ESC_CHAR;
fragment ALPHA: 'a'..'z' | 'A'..'Z';
fragment UNDERSCORE: '_';

// String literal

STR_LITER: '"' CHARACTER* '"';

// Character literal

CHAR_LITER: '\'' CHARACTER '\'';

// Pairs

NULL: 'null';
PAIR: 'pair';
COMMA: ',';
FST: 'fst';
SND: 'snd';
NEWPAIR: 'newpair';

// Base types

INT_T: 'int';
BOOL_T: 'bool';
CHAR_T: 'char';
STRING_T: 'string';

// Functions

CALL: 'call';
IS: 'is';

// Statements

SKIP_STAT: 'skip';
ASSIGN: '=';
READ: 'read';
FREE: 'free';
RETURN: 'return';
EXIT: 'exit';
PRINT: 'print';
PRINTLN: 'println';
IF: 'if';
THEN: 'then';
ELSE: 'else';
ENDIF: 'fi';
WHILE: 'while';
DO: 'do';
ENDWHILE: 'done';
BEGIN: 'begin';
END: 'end';
SEMICOLON: ';';

// Identifier

IDENT: (UNDERSCORE | ALPHA) (UNDERSCORE | ALPHA | DIGIT)*;