package org.jetbrains.plugins.clojure.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.UsefulTestCase;
import org.junit.Assert;

/**
 * @author ilyas
 */
@SuppressWarnings("SpellCheckingInspection")
public class LexerTest extends UsefulTestCase {

  public void testNumeric_literals() {
    doTest("(+ 123 1N 1. 1.2 1e2 1M 1.2M 1e2M)", "( {(}\n" +
        "atom {+}\n" +
        "WHITE_SPACE { }\n" +
        "long literal {123}\n" +
        "WHITE_SPACE { }\n" +
        "big integer literal {1N}\n" +
        "WHITE_SPACE { }\n" +
        "double literal {1.}\n" +
        "WHITE_SPACE { }\n" +
        "double literal {1.2}\n" +
        "WHITE_SPACE { }\n" +
        "double literal {1e2}\n" +
        "WHITE_SPACE { }\n" +
        "big deciamel literal {1M}\n" +
        "WHITE_SPACE { }\n" +
        "big deciamel literal {1.2M}\n" +
        "WHITE_SPACE { }\n" +
        "big deciamel literal {1e2M}\n" +
        ") {)}");
  }

  public void testKeyword() {
    doTest(":sdfsd/sdfsdf/sdfsdf", "key {:sdfsd/sdfsdf/sdfsdf}");
  }

  public void testKeyword2() {
    doTest(":123 :%abc", "key {:123}\nWHITE_SPACE { }\nkey {:%abc}");
  }

  public void testKeyword3() {
    doTest(":fadfa/adfasdf:dafasdf/asdfad", "key {:fadfa/adfasdf:dafasdf/asdfad}");
  }

  public void testKeyword4() {
    doTest(":fadf#adfasdf", "key {:fadf#adfasdf}");
  }

  public void testInteger_radix() {
    doTest("36rXYZ", "long literal {36rXYZ}");
  }

  public void testBadKeyword() {
    doTest(":,", "BAD_CHARACTER {:}\n" +
        ", {,}");
  }

  private void doTest(String fileText, String tokens) {
    Lexer lexer = new ClojureFlexLexer();
    lexer.start(fileText.trim());

    StringBuilder buffer = new StringBuilder();

    IElementType type;
    while ((type = lexer.getTokenType()) != null) {
      CharSequence s = lexer.getBufferSequence();
      s = s.subSequence(lexer.getTokenStart(), lexer.getTokenEnd());
      buffer.append(type.toString()).append(" {").append(s).append("}");
      lexer.advance();
      if (lexer.getTokenType() != null) {
        buffer.append("\n");
      }
    }

    Assert.assertEquals(tokens, buffer.toString());
  }

}