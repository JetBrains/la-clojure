package org.jetbrains.plugins.clojure.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.clojure.BaseClojureFileSetTestCase;

/**
 * @author ilyas
 */
public class LexerTest extends BaseClojureFileSetTestCase {

  @NonNls
  private static final String DATA_PATH = "test/org/jetbrains/plugins/clojure/lexer/data";

  public LexerTest() {
    super(System.getProperty("path") != null ?
        System.getProperty("path") :
        DATA_PATH
    );
  }


  public String transform(String testName, String[] data) throws Exception {
    String fileText = data[0];

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

    System.out.println("------------------------ " + testName + " ------------------------");
    System.out.println(buffer.toString());
    System.out.println();

    return buffer.toString();

  }

  public static Test suite() {
    return new LexerTest();
  }


}