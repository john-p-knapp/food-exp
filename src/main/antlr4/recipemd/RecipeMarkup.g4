grammar RecipeMarkup ;

recipe: titleEntry (linkEntry|note|compDef|instruction|tagEntry|imageEntry)+ ;

compDef: LBRACE LBRACKET name+=WORD+ RBRACKET (instruction|note|imageEntry)+ RBRACE;

instruction: HASH (dirText|combination|measuredAddition|measuredCompRef|addition|compRef)+ ;

combination: 'combine:' LBRACE (measuredAddition|measuredCompRef|addition|compRef)+ RBRACE;

measuredAddition: LBRACKET amount=NUMBER unit=('item'|'sprig'|'tsp'|'tbl'|'cup'|'lbs'|'oz'|'ml'|'g'|'dash'|'cloves'|'head'|'gallon') (LBRACKET prep+=WORD+ RBRACKET)? ingredient+=WORD+ (LBRACKET postprep+=WORD+ RBRACKET)? RBRACKET;
measuredCompRef: LBRACKET amount=NUMBER unit=('item'|'sprig'|'tsp'|'tbl'|'cup'|'lbs'|'oz'|'ml'|'g'|'dash'|'cloves'|'head'|'gallon') DOLLAR LBRACKET name+=WORD+ RBRACKET RBRACKET;
addition: LBRACKET ingredient+=WORD+ RBRACKET;
compRef: DOLLAR LBRACKET name+=WORD+ RBRACKET;

note : STAR dirText;
titleEntry: TITlE name=WORD+;
linkEntry: LINK url=WORD;
imageEntry: IMAGE path=WORD;
tagEntry: TAGS tags=WORD+;
dirText: (NUMBER|WORD)+;

// Lexer
TITlE: 'TITLE';
LINK: 'LINK';
IMAGE: 'IMAGE';
TAGS: 'TAGS';

//PERCENT: '%';
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

