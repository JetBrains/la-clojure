package org.jetbrains.plugins.clojure.completion;

import java.io.IOException;

/**
 * @author Alefas
 * @since 29.04.13
 */
public class ClojureBasicCompletionTest extends ClojureCompletionTestBase {
  public void testSimpleClassName() throws IOException {
    String fileText =
        "(def foo (fn [y xsxxx<caret>]))";
    configureFromFileText("dummy.clj", fileText);
    final CompleteResult complete = complete();
    assertNull(complete);
  }
}
