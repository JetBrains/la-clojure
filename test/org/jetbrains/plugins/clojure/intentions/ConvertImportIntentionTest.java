package org.jetbrains.plugins.clojure.intentions;

/**
 * @author Stanislav.Osipov
 * @since 8/8/13
 */
public class ConvertImportIntentionTest extends ClojureIntentionTestBase {

  public void testImport1() throws Exception {
    doTest();
  }

  public void testImport2() throws Exception {
    doTest();
  }

  public void testImport3() throws Exception {
    doTest();
  }

  public void testImport4() throws Exception {
    assertNotAvailable();
  }

  public void testImport5() throws Exception {
    assertNotAvailable();
  }

  @Override
  protected String getIntentionName() {
    return "(Un)Quote statement";
  }
}
