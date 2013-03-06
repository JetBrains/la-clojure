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

  public void testRequire1() throws Exception {
    checkResolveToCapitalize("requireNs/require1.clj");
  }

  public void testRequire2() throws Exception {
    checkResolveToCapitalize("requireNs/require2.clj");
  }

  public void testRequire3() throws Exception {
    checkResolveToCapitalize("requireNs/require3.clj");
  }

  public void testRequire4() throws Exception {
    checkResolveToCapitalize("requireNs/require4.clj");
  }

  public void testRequire5() throws Exception {
    checkResolveToCapitalize("requireNs/require5.clj");
  }

  public void testRequire6() throws Exception {
    checkResolveToCapitalize("requireNs/require6.clj");
  }

  private void checkResolveToDate(String filePath) throws IOException {
    configureByFileName(filePath);
    final PsiReference reference = findReference();
    final PsiElement element = reference.resolve();
    assert(element instanceof PsiClass && ((PsiClass) element).getQualifiedName().equals("java.util.Date"));
  }

  private void checkResolveToCapitalize(String filePath) throws IOException {
    configureByFileName(filePath);
    final PsiReference reference = findReference();
    final PsiElement element = reference.resolve();
    assert(element instanceof ClDef && ((ClDef) element).getName().equals("is"));
  }

  private void checkResolveToSplit(String filePath) throws IOException {
    configureByFileName(filePath);
    final PsiReference reference = findReference();
    final PsiElement element = reference.resolve();
    assert(element instanceof ClDef && ((ClDef) element).getName().equals("split"));
  }

  public void testUse1() throws Exception {
    checkResolveToSplit("use/use1.clj");
  }

  public void testUse2() throws Exception {
    checkResolveToSplit("use/use2.clj");
  }

  public void testUse3() throws Exception {
    checkResolveToSplit("use/use3.clj");
  }

  public void testUse4() throws Exception {
    checkResolveToSplit("use/use4.clj");
  }

  public void testUse5() throws Exception {
    checkResolveToSplit("use/use5.clj");
  }

  public void testUse6() throws Exception {
    checkResolveToSplit("use/use6.clj");
  }

  public void testUse7() throws Exception {
    checkResolveToSplit("use/use7.clj");
  }

  public void testUse8() throws Exception {
    checkResolveToSplit("use/use8.clj");
  }

  public void testUse9() throws Exception {
    checkResolveToSplit("use/use9.clj");
  }

  public void testUse10() throws Exception {
    checkResolveToSplit("use/use10.clj");
  }

  public void testUse11() throws Exception {
    checkResolveToSplit("use/use11.clj");
  }

  public void testUse12() throws Exception {
    checkResolveToSplit("use/use12.clj");
  }

  public void testUse13() throws Exception {
    checkResolveToSplit("use/use13.clj");
  }

  public void testUse14() throws Exception {
    checkResolveToSplit("use/use14.clj");
  }

  public void testUse15() throws Exception {
    checkResolveToSplit("use/use15.clj");
  }

  public void testUse16() throws Exception {
    checkResolveToSplit("use/use16.clj");
  }

  public void testUseFails1() throws Exception {
    checkReferenceIsUnresolved("use/useFailed1.clj");
  }

  public void testUseFails2() throws Exception {
    checkReferenceIsUnresolved("use/useFailed2.clj");
  }

  public void testUseFails4() throws Exception {
    checkReferenceIsUnresolved("use/useFailed4.clj");
  }

  public void testUseFails5() throws Exception {
    checkReferenceIsUnresolved("use/useFailed5.clj");
  }

  public void testImportFails1() throws Exception {
    checkReferenceIsUnresolved("javaClass/importFails1.clj");
  }

  public void testImportFails2() throws Exception {
    checkReferenceIsUnresolved("javaClass/importFails2.clj");
  }

  public void testRequireFails1() throws Exception {
    checkReferenceIsUnresolved("requireNs/requireFails1.clj");
  }

  public void testRequireFails2() throws Exception {
    checkReferenceIsUnresolved("requireNs/requireFails2.clj");
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
