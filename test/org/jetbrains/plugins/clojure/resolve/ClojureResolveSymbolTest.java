package org.jetbrains.plugins.clojure.resolve;

import com.intellij.psi.PsiReference;
import org.jetbrains.plugins.clojure.util.TestUtils;

/**
 * @author ilyas
 */
public class ClojureResolveSymbolTest extends ClojureResolveTestCaseBase {

  @Override
  public String getTestDataPath() {
    return TestUtils.getTestDataPath() + "/resolve/";
  }

  public void doTest(String fileName) throws Exception {
    final PsiReference reference = configureByFile(getTestName(true) + "/" + fileName);
    assertNotNull(reference);
    // todo implement me!
  }

  private void doSimpleTest() throws Exception {
    doTest("my_namespace.clj");
  }

  public void testUseNs() throws Exception {
    doSimpleTest();
  }

}
