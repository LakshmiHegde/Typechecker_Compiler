import java_cup.runtime.Symbol;
import java.io.FileInputStream;
import java.io.InputStream;

%%

%class IdLexer
%line
%column

%cup
%{

%}

//%type Symbol
digit = [0-9]
letter = [a-zA-Z]
id = {letter}({letter}|{digit})*
num = {digit}+
type = "num" | "bool" 
boolconst = "true" | "false"
returnK= "return"
LineTerminator = [\r|\n|\r\n]
InputCharacter = [^\r\n]
whitespace     = {LineTerminator} | [ \t\f]
/* %type Token
%eofval{
System.out.println("reached");
return new Token(null);
%eofval}
*/
%%
{type} {
  if(yytext().equals("num")) 
  {
    	return new Symbol(sym.TYPE, yyline, yycolumn, new NumType());
  }
  else if(yytext().equals("bool")) 
  {	
    	return new Symbol(sym.TYPE, yyline, yycolumn, new BoolType());
  }

}

{boolconst} {return new Symbol(sym.BOOLCONST, yyline, yycolumn, Boolean.parseBoolean(yytext())); }
"if" {return new Symbol(sym.IF, yyline, yycolumn, null); }
"then" { return new Symbol(sym.THEN, yyline, yycolumn, null); }
"else" { return new Symbol(sym.ELSE, yyline, yycolumn, null); }
"loop" { return new Symbol(sym.LOOP, yyline, yycolumn, null); }
{returnK} { return new Symbol(sym.RET, yyline, yycolumn, null); }
{id} { return new Symbol(sym.ID, yyline, yycolumn, yytext()); }
{num} { return new Symbol(sym.NUMBER, yyline, yycolumn, Integer.parseInt(yytext())); }
"=" { return new Symbol(sym.ASSIGN, yyline, yycolumn, null); }
"+" { return new Symbol(sym.PLUS, yyline, yycolumn, null); }
"-" {  return new Symbol(sym.MINUS, yyline, yycolumn, null); }
"*" { return new Symbol(sym.TIMES, yyline, yycolumn, null); }
"," {  return new Symbol(sym.COMMA, yyline, yycolumn, null); }
"(" {  return new Symbol(sym.LPAREN, yyline, yycolumn, null); }
")" {  return new Symbol(sym.RPAREN, yyline, yycolumn, null); }
"{" {  return new Symbol(sym.LFB, yyline, yycolumn, null); }
"}" { return new Symbol(sym.RFB, yyline, yycolumn, null); }
{whitespace}+ { /* skip white spaces */ }
[^]              { throw new Error("Illegal character <"+yytext()+">"); }
