package org.jetbrains.plugins.clojure.completion;

import java.io.IOException;

/**
 * @author Alefas
 * @since 16.01.13
 */
public class ClojureClassNameCompletionTest extends ClojureCompletionTestBase {
  public void testSimpleClassName() throws IOException {
    String fileText =
        "(ArrayList<caret>)";
    configureFromFileText("dummy.clj", fileText);
    final CompleteResult complete = complete(2);
    String resultText =
        "(ns dummy.clj\n" +
            "  (:import [java.util ArrayList]))\n" +
            "\n" +
            "(ArrayList<caret>)";
    completeLookupItem(complete.getElements()[0]);
    checkResultByText(resultText);
  }

  public void testMoreClassName() throws IOException {
    String fileText =
        "(ns dummy.clj\n" +
            "  (:import [java.util ArrayList]))\n" +
            "\n" +
            "(Iterator<caret>)";
    configureFromFileText("dummy.clj", fileText);
    final CompleteResult complete = complete(2);
    String resultText =
        "(ns dummy.clj\n" +
            "  (:import [java.util ArrayList Iterator]))\n" +
            "\n" +
            "(Iterator<caret>)";
    completeLookupItem(complete.getElements()[0]);
    checkResultByText(resultText);
  }

  public void testClassNameInImport() throws IOException {
    String fileText =
        "(import ArrayList<caret>)";
    configureFromFileText("dummy.clj", fileText);
    final CompleteResult complete = complete(2);
    String resultText =
        "(import java.util.ArrayList<caret>)";
    completeLookupItem(complete.getElements()[0]);
    checkResultByText(resultText);
  }
}
