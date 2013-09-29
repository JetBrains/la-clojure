package org.jetbrains.plugins.clojure.actions.editor;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * @author peter
 */
public class ClojureEditingTest extends LightCodeInsightFixtureTestCase {

  public void testEnterInMultipleSemicolonComment() {
    myFixture.configureByText("a.clj", " ;;; hello<caret>world");
    myFixture.type('\n');
    myFixture.checkResult(" ;;; hello\n ;;; <caret>world");
  }

  public void testEnterInLet() {
    myFixture.configureByText("a.clj", "(let [foo bar]<caret>\n\n)");
    myFixture.type('\n');
    myFixture.checkResult("(let [foo bar]\n  <caret>\n\n)");
  }

}
