grammar RecipeMarkup ;

recipe: titleEntry (linkEntry|note|compDef|instruction)+ ;

compDef: LBRACE LBRACKET name+=WORD+ RBRACKET (instruction|note)+ RBRACE;
compRef: DOLLAR LBRACKET name+=WORD+ RBRACKET;

instruction: HASH (measuredAddition|addition|compRef|dirText)+ ;

measuredAddition: LBRACKET amount=NUMBER unit=('item'|'tsp'|'tbl'|'cup'|'lbs'|'oz'|'ml'|'g'|'dash'|'cloves'|'head') (LBRACKET prep+=WORD+ RBRACKET)? ingredient+=WORD+ (LBRACKET postprep+=WORD+ RBRACKET)? RBRACKET;
addition: LBRACKET ingredient+=WORD+ RBRACKET;

note : STAR dirText ;


titleEntry: TITlE name=WORD+;
linkEntry: LINK url=WORD;
dirText: (NUMBER|WORD)+;

// Lexer
TITlE: 'TITLE';
LINK: 'LINK';

LBRACKET: '[';
RBRACKET: ']';
LBRACE: '{';
RBRACE: '}';
DOLLAR: '$';
STAR: '*';
HASH : '#';
NUMBER : ( DIGIT+ (. DIGIT+)?) | (DIGIT+ '/' DIGIT+);

WORD : (LETTER|DIGIT|PUNC)+ ;


WS : [ \n\r\t]+ -> skip ;

fragment PUNC
	: ['.,():/\-;&];

fragment LETTER
	: [a-zA-Z_];

fragment DIGIT
	: [0-9] ;

