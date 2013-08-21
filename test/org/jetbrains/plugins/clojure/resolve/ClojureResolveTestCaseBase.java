package org.jetbrains.plugins.clojure.resolve;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.plugins.clojure.base.ClojureLightPlatformCodeInsightTestCase;
import org.jetbrains.plugins.clojure.util.TestUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author ilyas
 */
public abstract class ClojureResolveTestCaseBase extends ClojureLightPlatformCodeInsightTestCase {
  public String folderPath() {
    return TestUtils.getTestDataPath() + "/";
  }

  private static String JDK_HOME = TestUtils.getMockJdk();

  public abstract String getTestDataPath();

  public String getTestFolderPath() {
    VirtualFile testDataRoot = LocalFileSystem.getInstance().findFileByPath(getTestDataPath());
    assertNotNull(testDataRoot);
    return testDataRoot.getPath() + File.separatorChar + getTestName(true) + File.separatorChar;
  }

  protected void configureByFileName(String fileName) throws IOException {
    String filePath = folderPath() + File.separator + fileName;
    File ioFile = new File(filePath);
    String fileText = FileUtil.loadFile(ioFile, CharsetToolkit.UTF8);
    fileText = StringUtil.convertLineSeparators(fileText);
    int offset = fileText.indexOf("<ref>");
    fileText = fileText.replace("<ref>", "");
    configureFromFileText(ioFile.getName(), fileText);
    if (offset != -1) {
      getEditor().getCaretModel().moveToOffset(offset);
    }
  }

  protected PsiReference findReference() {
    return getFile().findReferenceAt(getEditor().getCaretModel().getOffset());
  }
}
