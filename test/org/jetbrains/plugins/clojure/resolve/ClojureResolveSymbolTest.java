package org.jetbrains.plugins.clojure.resolve;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.impl.ns.ClSyntheticNamespace;
import org.jetbrains.plugins.clojure.util.TestUtils;

import java.io.IOException;

/**
 * @author ilyas
 */
public class ClojureResolveSymbolTest extends ClojureResolveTestCaseBase {

  @Override
  public String getTestDataPath() {
    return TestUtils.getTestDataPath() + "/resolve/";
  }

  @Override
  public String folderPath() {
    return super.folderPath() + "/resolve/";
  }

  private String commonTestFile() {
    return getTestName(true) + "/my_namespace.clj";
  }

  private PsiElement resolveReference() throws Exception {
    configureByFileName(commonTestFile());
    final PsiElement element = findReference().resolve();
    assertNotNull(element);
    return element;
  }

  private PsiElement resolveReference(String testPath) throws Exception {
    configureByFileName(testPath);
    final PsiElement element = findReference().resolve();
    assertNotNull(element);
    return element;
  }

  public void testDeftest1() throws Exception {
    final PsiElement element = resolveReference("useNs/deftest1.clj");
    assertTrue(element instanceof ClDef);
    assertTrue("deftest".equals(((ClDef) element).getName()));
  }

  public void testDeftest2() throws Exception {
    final PsiElement element = resolveReference("useNs/deftest2.clj");
    assertTrue(element instanceof ClDef);
    assertTrue("deftest".equals(((ClDef) element).getName()));
  }

  public void testDeftest3() throws Exception {
    final PsiElement element = resolveReference("useNs/deftest3.clj");
    assertTrue(element instanceof ClDef);
    assertTrue("deftest".equals(((ClDef) element).getName()));
  }

  public void testDeftest4() throws Exception {
    final PsiElement element = resolveReference("useNs/deftest4.clj");
    assertTrue(element instanceof ClDef);
    assertTrue("deftest".equals(((ClDef) element).getName()));
  }

  public void testDeftest5() throws Exception {
    checkReferenceIsUnresolved("useNs/deftest5.clj");
  }

  public void testDeftest6() throws Exception {
    final PsiElement element = resolveReference("useNs/deftest6.clj");
    assertTrue(element instanceof ClDef);
    assertTrue("deftest".equals(((ClDef) element).getName()));
  }

  // Actual test cases

  public void testUseNs() throws Exception {
    final PsiElement element = resolveReference();
    assertTrue(element instanceof ClDef);
    assertTrue("encode-str".equals(((ClDef) element).getName()));
  }

  public void testUseNsMany() throws Exception {
    final PsiElement element = resolveReference();
    assertTrue(element instanceof ClDef);
    assertTrue("collection-tag".equals(((ClDef) element).getName()));
  }

  public void testUseNsResolve() throws Exception {
    final PsiElement element = resolveReference();
    assertTrue(element instanceof ClSyntheticNamespace);
    assertTrue("clojure.inspector".equals(((ClSyntheticNamespace) element).getQualifiedName()));
  }


  public void testRequireNs() throws Exception {
    final PsiElement element = resolveReference();
    assertTrue(element instanceof ClSyntheticNamespace);
  }

  public void testRequireSymbol() throws Exception {
    final PsiElement element = resolveReference();
    assertTrue(element instanceof ClDef);
    assertTrue("trace-fn-call".equals(((ClDef) element).getName()));
  }

  public void testImport1() throws Exception {
    checkResolveToDate("javaClass/import1.clj");
  }

  public void testImport2() throws Exception {
    checkResolveToDate("javaClass/import2.clj");
  }

  public void testImport3() throws Exception {
    checkResolveToDate("javaClass/import3.clj");
  }

  public void testImport4() throws Exception {
    checkResolveToDate("javaClass/import4.clj");
  }

  public void testImport5() throws Exception {
    checkResolveToDate("javaClass/import5.clj");
  }

  public void testImport6() throws Exception {
    checkResolveToDate("javaClass/import6.clj");
  }

  public void testImport7() throws Exception {
    checkResolveToDate("javaClass/import7.clj");
  }

  private void checkResolveToDate(String filePath) throws IOException {
    configureByFileName(filePath);
    final PsiReference reference = findReference();
    final PsiElement element = reference.resolve();
    assert(element instanceof PsiClass && ((PsiClass) element).getQualifiedName().equals("java.util.Date"));
  }

  public void testImportFails1() throws Exception {
    checkReferenceIsUnresolved("javaClass/importFails1.clj");
  }

  public void testImportFails2() throws Exception {
    checkReferenceIsUnresolved("javaClass/importFails2.clj");
  }

  private void checkReferenceIsUnresolved(String filePath) throws IOException {
    configureByFileName(filePath);
    final PsiReference reference = findReference();
    final PsiElement element = reference.resolve();
    assertNull(element);
  }

  public void testImportFails3() throws Exception {
    checkReferenceIsUnresolved("javaClass/importFails3.clj");
  }

  public void testJavaClass() throws Exception {
    configureByFileName(commonTestFile());
    final PsiReference reference = findReference();
    if (reference instanceof PsiMultiReference) {
      PsiMultiReference multiReference = (PsiMultiReference) reference;
      for (ResolveResult result : multiReference.multiResolve(false)) {
        final PsiElement element = result.getElement();
        if (element instanceof PsiClass) {
          PsiClass clazz = (PsiClass) element;
          if (clazz.getName().equals("Arrays")) {
            return;
          }
        }
      }
      assertTrue("No 'Arrays' class found", false);

    } else {
      assertTrue(false);
    }
  }

}
