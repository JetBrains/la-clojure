package org.jetbrains.plugins.clojure.annotator;

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.clojure.ClojureBaseTestCase;
import org.jetbrains.plugins.clojure.highlighter.ClojureSyntaxHighlighter;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClojureAnnotatorTest extends ClojureBaseTestCase {

  private static final String DATA_PATH = System.getProperty("user.dir") + "/testdata/annotator/";

  @Override
  public String getDataPath() {
    return DATA_PATH;
  }

  private void doAnnotationTest(PsiElement element, PsiElement elementWithAnnotation, TextAttributesKey attributesKey) {

    AnnotationSession annotationSession = mock(AnnotationSession.class);
    when(annotationSession.getFile()).thenReturn(elementWithAnnotation.getContainingFile());

    AnnotationHolderImpl annotationHolder = new AnnotationHolderImpl(annotationSession);

    ClojureAnnotator annotator = new ClojureAnnotator();

    annotator.annotate(element, annotationHolder);

    if (attributesKey == null) {
      assertEmpty(annotationHolder);
    } else {
      assertTrue(containsAnnotation(attributesKey, annotationHolder));
    }
  }

  //TODO this isn't testing anything. Need to add text offsets to determine it's adding the correct text
  private boolean containsAnnotation(TextAttributesKey attributesKey, AnnotationHolderImpl annotationHolder) {
    for (Annotation annotation : annotationHolder) {
      if (annotation.getTextAttributes().equals(attributesKey)) {
        return true;
      }
    }
    return false;
  }

  private PsiFile getPsiFile(String fileName) {
    String contents = fetchFile("", fileName, TEST_FILE_EXT);
    return createPseudoPhysicalFile(getProject(), "test.clj", contents);
  }

  @Test
  public void testAnnotateCallOperator() throws ClassNotFoundException {
    PsiFile psiFile = getPsiFile("annotateCallOperator");
    doAnnotationTest(psiFile.getFirstChild(), psiFile.getFirstChild().getFirstChild(), ClojureSyntaxHighlighter.CALL_OPERATOR);
  }

  @Test
  public void testDontAnnotateCallOperatorInQuotedList() throws ClassNotFoundException {
    PsiFile file = getPsiFile("dontAnnotateCallOperatorInQuotedList");
    ClList list = (ClList) file.getFirstChild().getFirstChild().getNextSibling();
    doAnnotationTest(list, list.getFirstSymbol(), null);
  }

  @Test
  public void testAnnotateDefWithGlobalVarNameDeclaration() {
    PsiFile file = getPsiFile("annotateDefWithGlobalVarNameDeclaration");
    ClDef def = (ClDef) file.getFirstChild();
    doAnnotationTest(def, def.getAllSymbols()[1], ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION);
  }

  @Test
  public void testAnnotateDeclareWithSymbolDeclaration() {
    PsiFile file = getPsiFile("annotateDeclareWithGlobalVarNameDeclaration");
    ClList list = (ClList) file.getFirstChild();
    doAnnotationTest(list, list.getAllSymbols()[2], ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION);
    doAnnotationTest(list, list.getAllSymbols()[3], ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION);
  }

  @Test
  public void testAnnotateInternWithGlobalVarNameDeclaration() {
    PsiFile file = getPsiFile("annotateInternWithGlobalVarNameDeclaration");
    //System.out.print(DebugUtil.psiToString(file, false));
    ClList list = (ClList) file.getFirstChild();
    doAnnotationTest(list, list.getChildren()[2], ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION);
  }

//  @Test
//  public void testThing() {
//    String contents = fetchFile("", "highlightFirstSymbol", TEST_FILE_EXT);
//    final PsiFile psiFile = createPseudoPhysicalFile(getProject(), "test.clj", contents);
//    final String psiTree = DebugUtil.psiToString(psiFile, false);
//    final AnnotationHolder annotationHolder = mock(AnnotationHolder.class);
//    PsiElementProcessor.FindElement<PsiElement> processor = new PsiElementProcessor.FindElement<PsiElement>() {
//      ClojureAnnotator annotator = new ClojureAnnotator();
//
//      @Override
//      public boolean execute(@NotNull PsiElement each) {
//        annotator.annotate(each, annotationHolder);
//        return true;
//      }
//    };
//    PsiTreeUtil.processElements(psiFile, processor);
//  }
}
