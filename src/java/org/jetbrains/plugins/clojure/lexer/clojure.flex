/*
 * Copyright 2000-2009 Red Shark Technology
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.clojure.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.*;
import java.io.CharArrayReader;
import org.jetbrains.annotations.NotNull;

%%

%class _ClojureLexer
%implements ClojureTokenTypes, FlexLexer
%unicode
%public

%function advance
%type IElementType

%eof{ return;
%eof}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// User code //////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

%{
  /*
  public final int getTokenStart(){
    return zzStartRead;
  }

  public final int getTokenEnd(){
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end,int initialState) {
    char [] buf = buffer.toString().substring(start,end).toCharArray();
    yyreset( new CharArrayReader( buf ) );
    yybegin(initialState);
  }
  
  public void reset(CharSequence buffer, int initialState){
    reset(buffer, 0, buffer.length(), initialState);
  }
  */
%}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// NewLines and spaces /////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mNL = \r | \n | \r\n                                    // NewLines
mWS = " " | \t | \f | {mNL}                       // Whitespaces
mCOMMA = ","

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////      integers and floats     /////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mHEX_DIGIT = [0-9A-Fa-f]
mDIGIT = [0-9]
mBIG_INT_SUFFIX = N
mBIG_DECIMAL_SUFFIX = M
mFLOAT_SUFFIX = f | F
mLONG_SUFFIX = l | L
mINT_SUFFIX = i | I
mDOUBLE_SUFFIX = d | D
mEXPONENT = (e | E)("+" | "-")?([0-9])+

mNUM_INT_PART =  0
 ( (x | X){mHEX_DIGIT}+
   | {mDIGIT}+
   | ([0-7])+
 )?
 | {mDIGIT}+ "r" ({mDIGIT} | {mLETTER})+
 | {mDIGIT}+

// Long
mNUM_LONG = {mNUM_INT_PART}

// BigInteger
mNUM_BIG_INT = {mNUM_INT_PART} {mBIG_INT_SUFFIX}

// Double
mNUM_DOUBLE = {mNUM_INT_PART} ( ("." {mDIGIT}* {mEXPONENT}?) | {mEXPONENT})

// BigDecimal
mNUM_BIG_DECIMAL = {mNUM_INT_PART} ( ("." {mDIGIT}* {mEXPONENT}? {mBIG_DECIMAL_SUFFIX})
 | {mEXPONENT} {mBIG_DECIMAL_SUFFIX} | {mBIG_DECIMAL_SUFFIX})

//Ratios
mRATIO = {mNUM_INT_PART} "/" {mNUM_INT_PART}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Parens, Squares, Curleys, Quotes /////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mLP = "("
mRP = ")"
mLS = "["
mRS = "]"
mLC = "{"
mRC = "}"

mQUOTE = "'"
mBACKQUOTE = "`"
mSHARP = "#"
mSHARPUP = {mSHARP} {mUP}
mUP = "^"
mIMPLICIT_ARG = "%" | "%"{mDIGIT}+ | "%""&"
mTILDA = "~"
mAT = "@"
mTILDAAT = {mTILDA} {mAT}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Strings /////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mONE_NL = \r | \n | \r\n
mHEX_DIGIT = [0-9A-Fa-f]

mCHAR = \\ [^" "\r\n]
    | \\ [:jletter:]+

mSTRING_ESC = \\ n | \\ r | \\ t | \\ b | \\ f | "\\" "\\" | \\ "$" | \\ \" | \\ \'
    | "\\""u"{mHEX_DIGIT}{4}
    | "\\" [0..3] ([0..7] ([0..7])?)?
    | "\\" [4..7] ([0..7])?
    | "\\" {mONE_NL}
    | {mCHAR}


mSTRING_CONTENT = ({mSTRING_ESC}|[^\\\"])*
mSTRING = \"\" | \" ([^\\\"] | {mSTRING_ESC})? {mSTRING_CONTENT} \"
mWRONG_STRING = \" ([^\\\"] | {mSTRING_ESC})? {mSTRING_CONTENT}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Comments ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mLINE_COMMENT = ";" [^\r\n]*

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////      identifiers      ////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mLETTER = [:jletter:]
mSLASH_LETTER = \\ ({mLETTER} | .)

mOTHER = "_" | "-" | "*" | "." | "+" | "=" | "&" | "<" | ">" | "$" | "/" | "?" | "!"
mNoDigit = ({mLETTER} | {mOTHER})

mOTHER_REDUCED = "_" | "-" | "*" | "+" | "=" | "&" | "<" | ">" | "$" | "?" | "!"
mNoDigit1 = ({mLETTER} | {mOTHER_REDUCED})

mIDENT = {mNoDigit} ({mNoDigit} | {mDIGIT} | "#" | {mQUOTE})*
mIDENT_KEY = ({mNoDigit} | "#") ({mNoDigit} | {mDIGIT} | "#" | {mQUOTE})*

//':' is also included here
mNOT_MACROS = [^\";@'\^`~()\\\[\]{}%\r\n\t\f ]
mNOT_MACROS_IDENT = ({mNOT_MACROS})+
mNOT_MACROS_AND_BACKSLASH = [^\";@'\^`~()\\\[\]{}%/\r\n\t\f ]
mNOT_MACROS_AND_BACKSLASH_IDENT = ({mNOT_MACROS_AND_BACKSLASH})+
mNOT_DIGIT_AND_BACKSLASH = [^0-9/]
mNOT_DIGIT_AND_BACKSLASH_IDENT = {mNOT_DIGIT_AND_BACKSLASH} ({mNOT_MACROS_AND_BACKSLASH})*

mKEY = ":" {mNOT_MACROS_IDENT} "/" {mNOT_DIGIT_AND_BACKSLASH_IDENT} |
       ":" {mNOT_MACROS_AND_BACKSLASH_IDENT}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////      predefined      ////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mNIL = "nil"
mTRUE = "true"
mFALSE = "false"

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////  states ///////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

%xstate SYMBOL

%%
<SYMBOL> {
  "."                                       {  return symDOT; }
  "/"                                       {  return symNS_SEP; }
  ({mNoDigit1} | {mDIGIT} | ":" | {mQUOTE})+           {  return symATOM; }
  (({mNoDigit1} | {mDIGIT} | ":" | {mQUOTE})+)? "#"    {  yybegin(YYINITIAL); return symATOM; }
  [^]                                       {  yypushback(yytext().length()); yybegin(YYINITIAL); }
}

<YYINITIAL>{

  {mLINE_COMMENT}                           {  return LINE_COMMENT; }
  
  {mWS}+                                    {  return WHITESPACE; }
  {mCOMMA}                                  {  return COMMA; }

  {mSTRING}                                 {  return STRING_LITERAL; }
  {mWRONG_STRING }                          {  return WRONG_STRING_LITERAL; }

  {mCHAR}                                   {  return CHAR_LITERAL; }
  {mNIL}                                    {  return NIL; }
  {mTRUE}                                   {  return TRUE; }
  {mFALSE}                                  {  return FALSE; }

  {mNUM_LONG}                               {  return LONG_LITERAL; }
  {mNUM_BIG_INT}                            {  return BIG_INT_LITERAL; }
  {mNUM_DOUBLE}                             {  return DOUBLE_LITERAL; }
  {mNUM_BIG_DECIMAL}                        {  return BIG_DECIMAL_LITERAL; }
  {mRATIO}                                  {  return RATIO; }

  // Reserved symbols
  "/"                                       {  return symATOM; }
  "."{mIDENT} | {mIDENT}"."                 {  return symATOM; }
  {mIDENT}                                  {  yypushback(yytext().length()); yybegin(SYMBOL); }
  {mKEY}                                    {  return COLON_SYMBOL; }


  {mQUOTE}                                  {  return QUOTE; }
  {mBACKQUOTE}                              {  return BACKQUOTE; }
  {mSHARPUP}                                {  return SHARPUP; }
  {mSHARP}                                  {  return SHARP; }
  {mUP}                                     {  return UP; }
  {mIMPLICIT_ARG}                           {  return symIMPLICIT_ARG; }
  {mTILDA}                                  {  return TILDA; }
  {mAT}                                     {  return AT; }
  {mTILDAAT}                                {  return TILDAAT; }


  {mLP}                                     {  return LEFT_PAREN; }
  {mRP}                                     {  return RIGHT_PAREN; }
  {mLS}                                     {  return LEFT_SQUARE; }
  {mRS}                                     {  return RIGHT_SQUARE; }
  {mLC}                                     {  return LEFT_CURLY; }
  {mRC}                                     {  return RIGHT_CURLY; }


}

// Anything else is should be marked as a bad char
.                                           {  return BAD_CHARACTER; }




