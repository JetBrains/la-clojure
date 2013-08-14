package org.jetbrains.plugins.clojure.intentions;

import com.intellij.testFramework.fixtures.CodeInsightTestUtil;
import org.jetbrains.plugins.clojure.base.ClojureBaseTestCase;

/**
 * @author Stanislav.Osipov
 * @since 8/8/13
 */
public abstract class ClojureIntentionTestBase extends ClojureBaseTestCase {

  private static final String DATA_PATH = System.getProperty("user.dir") + "/testData/intention";

  @Override
  public String getDataPath() {
    return DATA_PATH;
  }

  protected abstract String getIntentionName();

  private void doTest(String intentionName) {
    final String testName = getTestName(false);
    CodeInsightTestUtil.doIntentionTest(myFixture, intentionName, testName + SOURCE_FILE_EXT, testName + TEST_FILE_EXT);
  }

  protected void doTest() {
    doTest(getIntentionName());
  }

  protected void assertNotAvailable() {
    assertNotAvailable(getIntentionName());
  }

  private void assertNotAvailable(String intentionName) {
    final String testName = getTestName(false);
    myFixture.configureByFile(testName + SOURCE_FILE_EXT);
    assertEmpty("Intention \'" + intentionName + "\' is available but should not",
        myFixture.filterAvailableIntentions(intentionName));
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

}
