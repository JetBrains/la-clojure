package org.jetbrains.plugins.clojure.refactoring;

import com.intellij.lang.LanguageRefactoringSupport;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.plugins.clojure.ClojureLanguage;

/**
 * @author Stanislav.Osipov
 * @since 8/23/13
 */
public class IntroduceVariableTest extends ClojureRefactoringTestBase {

  @Override
  protected RefactoringActionHandler getRefactoringActionHandler() {
    return LanguageRefactoringSupport.INSTANCE.findSingle(ClojureLanguage.getInstance()).getIntroduceVariableHandler();
  }

  public void testSimpleSelection() throws Exception {
    doTest();
  }

  public void testContainerChooser() throws Exception {
    doTest();
  }

  public void testGuard() throws Exception {
    doTest();
  }

  public void testFuzzySelection() throws Exception {
    doTest();
  }

  public void testCaret() throws Exception {
    doTest();
  }

  public void testLetAddition() throws Exception {
    doTest();
  }

  public void testOccurences() throws Exception {
    doTest();
  }


}
