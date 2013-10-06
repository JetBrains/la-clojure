package org.jetbrains.plugins.clojure.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * @author peter
 */
public class RenameTest extends LightCodeInsightFixtureTestCase {

  public void testDash() {
    myFixture.configureByText("a.clj", "(let [a-<caret>b 2] a-b)");
    PsiElement parameter = myFixture.getFile().findReferenceAt(myFixture.getEditor().getCaretModel().getOffset()).resolve();
    myFixture.renameElement(parameter, "newName-c", true, true);
    myFixture.checkResult("(let [ne<caret>wName-c 2] newName-c)");
  }
  
}
