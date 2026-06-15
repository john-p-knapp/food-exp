grammar RecipeMarkup ;

recipe: titleEntry (linkEntry|note|compDef|instruction|tagEntry|imageEntry)+ ;

compDef: LBRACE LBRACKET name+=word+ RBRACKET (instruction|note|imageEntry)+ RBRACE;

instruction: HASH (dirText|combination|measuredAddition|measuredCompRef|addition|compRef)+ ;

combination: 'combine:' LBRACE (measuredAddition|measuredCompRef|addition|compRef)+ RBRACE;

measuredAddition: LBRACKET amount=NUMBER unit=unitKw (LBRACKET prep+=word+ RBRACKET)? ingredient+=word+ (LBRACKET postprep+=word+ RBRACKET)? RBRACKET;
measuredCompRef: LBRACKET amount=NUMBER unit=unitKw DOLLAR LBRACKET name+=word+ RBRACKET RBRACKET;
addition: LBRACKET ingredient+=word+ RBRACKET;
compRef: DOLLAR LBRACKET name+=word+ RBRACKET;

note : STAR dirText;
titleEntry: TITlE name=word+;
linkEntry: LINK url=WORD;
imageEntry: IMAGE path=WORD;
tagEntry: TAGS tags=word+;
dirText: (NUMBER|word)+;
unitKw: ITEM|SPRIG|STALK|TSP|TBL|CUP|LBS|OZ|ML|G|DASH|CLOVES|HEAD|GALLON;
word: WORD | unitKw ;

// Lexer
TITlE: 'TITLE';
LINK: 'LINK';
IMAGE: 'IMAGE';
TAGS: 'TAGS';

// Unit tokens. Defined explicitly (rather than as inline string literals in the
// unitKw parser rule) and placed before WORD so they win equal-length lexer ties 
ITEM: 'item';
SPRIG: 'sprig';
STALK: 'stalk';
TSP: 'tsp';
TBL: 'tbl';
CUP: 'cup';
LBS: 'lbs';
OZ: 'oz';
ML: 'ml';
G: 'g';
DASH: 'dash';
CLOVES: 'cloves';
HEAD: 'head';
GALLON: 'gallon';

//PERCENT: '%';
LBRACKET: '[';
RBRACKET: ']';
LBRACE: '{';
RBRACE: '}';
DOLLAR: '$';
STAR: '*';
HASH : '#';
NUMBER : ( DIGIT+ ('.' DIGIT+)?) | (DIGIT+ '/' DIGIT+);

WORD : (LETTER|DIGIT|PUNC)+ ;


WS : [ \n\r\t]+ -> skip ;

fragment PUNC
	: ['.,():/\-;&!];

fragment LETTER
	: [a-zA-Z_];

fragment DIGIT
	: [0-9] ;

