parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

program: BEGIN func* stat END EOF;

func: type ident L_PARENTHESIS paramList? R_PARENTHESIS IS stat END;

paramList: param (COMMA param)*;

param: type ident;

stat: SKIP_STAT                      #statSkip
| type ident ASSIGN assignRhs        #statDeclare
| assignLhs ASSIGN assignRhs         #statAssign
| READ assignLhs                     #statRead
| FREE expr                          #statSimple
| RETURN expr                        #statSimple
| EXIT expr                          #statSimple
| PRINT expr                         #statSimple
| PRINTLN expr                       #statSimple
| IF expr THEN stat ELSE stat ENDIF  #statIf
| WHILE expr DO stat ENDWHILE        #statWhile
| BEGIN stat END                     #statBegin
| stat SEMICOLON stat                #statMulti
;

assignLhs: ident
| arrayElem
| pairElem
| pointerElem;

assignRhs: expr
| arrayLiter
| NEWPAIR L_PARENTHESIS expr COMMA expr R_PARENTHESIS
| pairElem
| CALL ident L_PARENTHESIS argList? R_PARENTHESIS;

argList: expr (COMMA expr)*;

pairElem: FST expr | SND expr;

pointerElem: MULT expr;

type: baseType | arrayType | pairType | pointerType;

baseType: INT_T | BOOL_T | CHAR_T | STRING_T;

arrayType: (baseType | pairType | L_PARENTHESIS pointerType R_PARENTHESIS) (L_BRACKET R_BRACKET)+;

pairType: PAIR L_PARENTHESIS pairElemType COMMA pairElemType R_PARENTHESIS;

pairElemType: baseType | arrayType | PAIR;

pointerType: (baseType | pairType | arrayType) MULT+;

expr: intLiter                     #exprSingle
| boolLiter                        #exprSingle
| charLiter                        #exprSingle
| strLiter                         #exprSingle
| pairLiter                        #exprSingle
| ident                            #exprSingle
| arrayElem                        #exprSingle
| unaryOper expr                   #exprUnOp
| expr binaryOper1 expr            #exprBinOp
| expr binaryOper2 expr            #exprBinOp
| expr binaryOper3 expr            #exprBinOp
| expr binaryOper4 expr            #exprBinOp
| expr binaryOper5 expr            #exprBinOp
| expr binaryOper6 expr            #exprBinOp
| L_PARENTHESIS expr R_PARENTHESIS #exprBrackets
;

unaryOper: NOT | MINUS | LEN | ORD | CHR | REF | MULT;

binaryOper1: MULT | DIV | MOD;

binaryOper2: PLUS | MINUS;

binaryOper3: GT | GTE | LT | LTE;

binaryOper4: EQ | NEQ;

binaryOper5: AND;

binaryOper6: OR;

arrayElem: ident (L_BRACKET expr R_BRACKET)+;

arrayLiter: L_BRACKET (expr (COMMA expr)* )? R_BRACKET;

boolLiter: TRUE | FALSE;

intLiter: (PLUS | MINUS)? INTEGER;

strLiter: STR_LITER;

charLiter: CHAR_LITER;

ident: IDENT;

pairLiter: NULL;