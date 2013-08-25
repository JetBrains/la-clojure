package org.jetbrains.plugins.clojure.refactoring;

import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.plugins.clojure.base.ClojureBaseTestCase;

/**
 * @author Stanislav.Osipov
 * @since 8/23/13
 */
public abstract class ClojureRefactoringTestBase extends ClojureBaseTestCase {

  private static final String DATA_PATH = System.getProperty("user.dir") + "/testdata/refactoring";

  @Override
  public String getDataPath() {
    return DATA_PATH;
  }

  protected void doTest() {
    String testName = getTestName(false);
    myFixture.configureByFile(testName + SOURCE_FILE_EXT);
    getRefactoringActionHandler().invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
    myFixture.checkResultByFile(testName + TEST_FILE_EXT);
  }

  protected abstract RefactoringActionHandler getRefactoringActionHandler();
}
