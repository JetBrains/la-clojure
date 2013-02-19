package org.jetbrains.plugins.clojure.resolve.psi;

import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubUpdatingIndex;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;

/**
 * @author peter
 */
public class StubTest extends LightCodeInsightFixtureTestCase {

  public void testDontParseUnrelatedNamespacesDuringResolve() {
    PsiFileImpl ns1 = (PsiFileImpl) myFixture.addFileToProject("ns1.clj", "(ns ns1)");
    PsiFileImpl ns2 = (PsiFileImpl) myFixture.addFileToProject("ns2.clj", "(ns ns2)");
    myFixture.configureByText("ns3.clj", "(require ns2)\n(ns2/foo)");

    myFixture.doHighlighting();
    assert !ns1.isContentsLoaded();
  }

  public void testDontParseTargetFileDuringDefnResolve() {
    PsiFileImpl used = (PsiFileImpl) myFixture.addFileToProject("used.clj", "(ns used)\n(defn foo [] (println \"hello\"))");
    myFixture.configureByText("main.clj", "(require used)\n(used/f<caret>oo)");

    PsiReference ref = myFixture.getFile().findReferenceAt(myFixture.getEditor().getCaretModel().getOffset());
    assertNotNull(ref);
    assertInstanceOf(ref.resolve(), ClDef.class);
    assert !used.isContentsLoaded();
  }
}
