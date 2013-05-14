package org.jetbrains.plugins.clojure.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.LocalTimeCounter;
import org.jetbrains.plugins.clojure.ClojureBaseTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author ilyas
 */
public class ClojureFormatterTest extends ClojureBaseTestCase {

  private static final String DATA_PATH = System.getProperty("user.dir") + "/testdata/formatter/";
  private static final String TEST_FILE_NAME = "test";

  public String getDataPath() {
    return DATA_PATH;
  }

  @Override
  protected PsiFile createPseudoPhysicalFile(Project project, String fileName, String text) throws IncorrectOperationException {
    String tmpFile = project.getBaseDir() + fileName;
    return PsiFileFactory.getInstance(project).createFileFromText(tmpFile,
        FileTypeManager.getInstance().getFileTypeByFileName(fileName),
        text,
        LocalTimeCounter.currentTime(),
        true);
  }

  public void doFormat() {
    final String testName = getTestName();

    final String contents = fetchFile("", testName, SOURCE_FILE_EXT);
    final String expected = fetchFile("", testName, TEST_FILE_EXT);

    final PsiFile psiFile = createPseudoPhysicalFile(getProject(), "test.clj", contents);
    final TextRange textRange = psiFile.getTextRange();

    CommandProcessor.getInstance().executeCommand(getProject(), new Runnable() {
      public void run() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          public void run() {
            CodeStyleManager.getInstance(getProject()).reformatText(psiFile, textRange.getStartOffset(), textRange.getEndOffset());
          }
        });
      }
    }, null, null);

    assertEquals(expected, psiFile.getText());
  }

  public void testClj_98() {
    doFormat();
  }

  public void testNameApostrophe() {
    doFormat();
  }

  public void testKeywordBeforeBrace() {
    doFormat();
  }
}
